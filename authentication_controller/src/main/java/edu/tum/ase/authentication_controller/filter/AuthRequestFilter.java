package edu.tum.ase.authentication_controller.filter;

import edu.tum.ase.authentication_controller.jwt.JwtUtil;
import edu.tum.ase.authentication_controller.model.AseUser;
import edu.tum.ase.authentication_controller.model.AseUserPrincipal;
import edu.tum.ase.authentication_controller.model.Project;
import edu.tum.ase.authentication_controller.repository.ProjectRepository;
import edu.tum.ase.authentication_controller.repository.UserRepository;
import edu.tum.ase.authentication_controller.service.AuthService;
import edu.tum.ase.authentication_controller.service.MongoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@Component
public class AuthRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MongoUserDetailsService mongoUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;
    @Autowired
    UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = null;
        String jwt = null;
        final String authHeader = request.getHeader("Authorization");
        System.out.println("Authenticate Header " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            jwt = authHeader.substring(7);
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                if (!jwtUtil.verifyJwtSignature(jwt)) {
                    response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
                }
            }catch (Exception e){
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
            }
        } else {
            // No valid authentication, No go
            if (authHeader == null || !authHeader.startsWith("Basic")) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "No JWT Token or Basic Auth Info Found");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // TODO: load a user from the database that has the same username
            // as in the JWT token.
            User userDetails = null;
            AseUser user = userRepository.findByName(username);
            userDetails = new AseUserPrincipal(user).getUser();

            authService.setAuthentication(userDetails, request);
            Authentication authContext = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(String.format("Authenticate Token Set:\n"
                            + "Username: %s\n"
                            + "Password: %s\n"
                            + "Authority: %s\n",
                    authContext.getPrincipal(),
                    authContext.getCredentials(),
                    authContext.getAuthorities().toString()));
        }
        filterChain.doFilter(request, response);
    }
}
