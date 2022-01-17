package edu.tum.ase.asedelivery.boxmanagement.filter;

import edu.tum.ase.asedelivery.boxmanagement.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AuthRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

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
                System.out.println("invalid sig");
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad JWT");
            return;
        }
        UsernamePasswordAuthenticationToken authentication = null;
        try {
            authentication = jwtUtil.getAuthentication(jwt, SecurityContextHolder.getContext().getAuthentication());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to generate authentication object :(");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Security Context not set!");
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
            return;
        }

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
//        }}