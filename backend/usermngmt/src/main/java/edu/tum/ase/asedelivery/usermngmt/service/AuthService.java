package edu.tum.ase.asedelivery.usermngmt.service;

import edu.tum.ase.asedelivery.asedeliverymodels.AseUser;
import edu.tum.ase.asedelivery.usermngmt.jwt.JwtUtil;
import edu.tum.ase.asedelivery.asedeliverymodels.UserRole;
import edu.tum.ase.asedelivery.usermngmt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class AuthService {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private PasswordEncoder bcryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService mongoUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    public String authenticateUser(String authorization, HttpServletRequest request) throws Exception {
        String username;
        String password;

        System.out.println("authenticate");
        // Get the username and password by decoding the Base64 credential inside
        if (authorization != null && authorization.startsWith("Basic")) {
            String encodedUsernamePassword = authorization.substring("Basic ".length()).trim();
            byte[] decodedUsernamePasswordBytes = Base64.getDecoder().decode(encodedUsernamePassword);
            String aux = new String(decodedUsernamePasswordBytes, StandardCharsets.UTF_8);
            String[] decodedUsernamePassword = aux.split(":");
            username = decodedUsernamePassword[0];
            password = decodedUsernamePassword[1];//bcryptPasswordEncoder.encode(decodedUsernamePassword[1]);
        } else {
            // Handle what happens if that isn't the case
            throw new Exception("The authorization header is either empty or isn't Basic.");
        }

        // find if there is any user exists in the database based on the credential
        final AseUser user = userRepository.findByName(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        if (user != null) {

            // find if there is any user exists in the database based on the credential.
            if (bcryptPasswordEncoder.matches(password, user.getPassword())) {
                // Remove the setAuthentication() as we do not want to keep the
                // user authenticated in the upcoming requests. The user has to attach
                // our generated JWT for every request they sent to the server.
                // setAuthentication(userDetails, request);


                final String jwt = jwtUtil.generateToken(user);
                System.out.println("jwt token generated: " + jwt);
                return jwt;
            } else {
                System.out.println("Invalid password");
                return "";
            }
            // Authenticate the user using the Spring Authentication Manager
            // Authentication asdf = authManager.authenticate(authenticationToken);
        }
        System.out.println("idk what happen");
        return "";
    }

    public UserRole getAuthenticatedUserRole(String authorization) {
        String username;

        //findout user via JWT Token
        if (authorization != null && authorization.startsWith("Basic")) {
            String encodedUsernamePassword = authorization.substring("Basic ".length()).trim();
            byte[] decodedUsernamePasswordBytes = Base64.getDecoder().decode(encodedUsernamePassword);
            String aux = new String(decodedUsernamePasswordBytes, StandardCharsets.UTF_8);
            String[] decodedUsernamePassword = aux.split(":");
            username = decodedUsernamePassword[0];
        } else {
            // Handle what happens if that isn't the case
            return null;
        }

        AseUser user = mongoUserDetailsService.findByName(username);

        return user.getRole();
    }

    public AseUser getAuthenticatedUser(Cookie[] cookies) {
        String username;
        String jwt;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("jwt")) {
                jwt = cookie.getValue();
                // If the JWT expires or not signed by us, send an error to the client
                if (jwtUtil.verifyJwtSignature(jwt)) {
                    // Extract the username from the JWT token.
                    username = jwtUtil.extractUsername(jwt);
                    return mongoUserDetailsService.findByName(username);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public void setAuthentication(User userDetails, HttpServletRequest request) {
        System.out.println("setAuthentication I guess");
    }
}