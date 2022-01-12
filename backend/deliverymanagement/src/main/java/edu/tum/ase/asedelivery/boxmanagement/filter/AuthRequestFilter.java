package edu.tum.ase.asedelivery.boxmanagement.filter;

import edu.tum.ase.asedelivery.boxmanagement.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

//    @Autowired
//    private UserService mongoUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

//        String path = request.getRequestURI();
//
//        if ("/auth".equals(path)) {
//            System.out.println("gottem");
//            filterChain.doFilter(request, response);
//            return;
//        }

        String username = null;
        String jwt = null;
        Cookie[] cookies = request.getCookies();
        Cookie jwtCookie = null;

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
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
                return;
            }
        }catch (Exception e){
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
            return;
        }


        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Security Context not set!");
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
            return;
        }
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
