package edu.tum.ase.asedelivery.usermngmt.controller;

import edu.tum.ase.asedelivery.usermngmt.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.tum.ase.asedelivery.usermngmt.jwt.JwtUtil;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    public ResponseEntity<String> newAuthentication(@RequestHeader("Authorization") String header, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Authentication of the user credentials
        String jwt = authService.authenticateUser(header, request);
        if (jwt.length() == 0){
            return new ResponseEntity<String>("Nope",HttpStatus.UNAUTHORIZED);
        }

        Cookie jwtCookie = new Cookie("jwt", jwt);

        // Configure the cookie to be HttpOnly
        jwtCookie.setHttpOnly(true);

        // and expires after a period
        jwtCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 days

        // Then include the cookie into the response
        response.addCookie(jwtCookie);
        return new ResponseEntity<String>("Here you go",HttpStatus.OK);
    }

    // Implement an Endpoint to find a project with a given name
    @RequestMapping(
            value = "",
            method = RequestMethod.GET
    )    @ResponseBody
    public String authGet() {
        return "Hya";
    }

}