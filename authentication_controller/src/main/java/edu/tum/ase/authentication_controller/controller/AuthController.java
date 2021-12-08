package edu.tum.ase.authentication_controller.controller;

import edu.tum.ase.authentication_controller.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<String> newAuthentication(@RequestHeader("Authorization") String header, HttpServletRequest request)
            throws Exception {
        // Authentication of the user credentials
        return authService.authenticateUser(header, request);
    }

    // Implement an Endpoint to find a project with a given name
    @GetMapping("")
    @ResponseBody
    public String getProjectByName() {
        return "Hi";
    }

}