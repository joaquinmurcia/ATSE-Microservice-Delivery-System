package edu.tum.ase.asedelivery.usermngmt.filter;

import edu.tum.ase.asedelivery.usermngmt.model.AseUser;
import edu.tum.ase.asedelivery.usermngmt.model.AseUserPrincipal;
import edu.tum.ase.asedelivery.usermngmt.jwt.JwtUtil;
import edu.tum.ase.asedelivery.usermngmt.repository.UserRepository;
import edu.tum.ase.asedelivery.usermngmt.service.AuthService;
import edu.tum.ase.asedelivery.usermngmt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserService mongoUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if ("/auth".equals(path)) {
            System.out.println("gottem");
            filterChain.doFilter(request, response);
            return;
        }
        if ("/error".equals(path)) {
            System.out.println("gottem");
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwt = null;
        Cookie[] cookies = request.getCookies();
        Cookie jwtCookie = null;

        if (cookies == null){
            System.out.println("No JWT found");
            response.sendError(HttpStatus.BAD_REQUEST.value(), "No JWT given");
            return;

        }

        for (Cookie tmp :cookies){
            if (tmp.getName().equals("jwt")){
                jwtCookie = tmp;
                break;
            }
        }
        if(jwtCookie == null) {
            System.out.println("No JWT found");
            response.sendError(HttpStatus.BAD_REQUEST.value(), "No JWT given");
            return;
        }

        jwt = jwtCookie.getValue();
        System.out.println("JWT token received: " + jwt);
        try {
            username = jwtUtil.extractUsername(jwt);
            if (!jwtUtil.verifyJwtSignature(jwt)) {
                System.out.println("Bad Signature");
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
                return;
            }
        }catch (Exception e){
            System.out.println("Bad Sig or no username ot error");
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
            return;
        }

//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // load a user from the database that has the same username as in the JWT token.
            User userDetails = null;
            AseUser user = userRepository.findByName(username);
            if (user == null){
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
                return;

            }

            userDetails = new AseUserPrincipal(user).getUser();
            authService.setAuthentication(userDetails, request);
            UsernamePasswordAuthenticationToken authentication = null;
            try {
                authentication = jwtUtil.getAuthentication(jwt, SecurityContextHolder.getContext().getAuthentication());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to generate authentication object :(");
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);

//        }
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(String.format("Authenticate Token Set:\n"
                        + "Username: %s\n"
                        + "Password: %s\n"
                        + "Authority: %s\n",
                authContext.getPrincipal(),
                authContext.getCredentials(),
                authContext.getAuthorities().toString()));

        filterChain.doFilter(request, response);
    }
//
//        @Override
//        protected boolean shouldNotFilter(HttpServletRequest request)
//                throws ServletException {
//            String path = request.getRequestURI();
//            return "/auth".equals(path) || "/auth/".equals(path);
//        }
}
