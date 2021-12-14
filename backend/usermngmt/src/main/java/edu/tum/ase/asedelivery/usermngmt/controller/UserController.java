package edu.tum.ase.asedelivery.usermngmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.tum.ase.asedelivery.usermngmt.model.AseUserDAO;
import edu.tum.ase.asedelivery.usermngmt.service.UserService;

/* ?? in User Management, where it says "create users from e-mail", does it mean
that the dispatcher will write the user data from e-mail or that it will be identified by
his/her email? */
@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
public class UserController {
    @Autowired
    private UserService userService;

    // TODO: add the other methods copying this structure (wait until you know
    // whether exceptions are graded)
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody AseUserDAO user) {
        // TODO: ?? Are exceptions graded?
        userService.createUser(user);
        return new ResponseEntity<>("user-created", HttpStatus.OK);
    }
}
