package edu.tum.ase.asedelivery.usermngmt.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.AseUser;
import edu.tum.ase.asedelivery.asedeliverymodels.AseUserPrincipal;
import edu.tum.ase.asedelivery.asedeliverymodels.Constants;
import edu.tum.ase.asedelivery.usermngmt.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import edu.tum.ase.asedelivery.usermngmt.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Controller
@CrossOrigin(origins="http://localhost:3000",allowCredentials="true")
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<AseUser>> createUsers(@RequestBody List<AseUser> users) {
        try {
            for (AseUser user : users) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }
            for (AseUser user : users) {
                if (!user.isValid()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            List<AseUser> _users = userService.saveAll(users);
            return new ResponseEntity<>(_users, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.GET
    )
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') || hasAuthority('ROLE_DELIVERER') || hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<AseUser>> getUsers() {
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<AseUser> users;
            Query query = new Query();

            /*if (payload.isPresent()) {
                if (!Validation.isNullOrEmpty(payload.get().getName())) {
                    query.addCriteria(Criteria.where(Constants.NAME).is(payload.get().getName()));
                }
                if (!Validation.isNullOrEmpty(payload.get().getRfidToken())) {
                    query.addCriteria(Criteria.where(Constants.RFID_TOKEN).is(payload.get().getRfidToken()));
                }
            }*/

            // User and deliverer can only access their own user information
            String authority = authContext.getAuthorities().toString();
            if (Stream.of("[ROLE_DELIVERER]","[ROLE_CUSTOMER]").anyMatch(authority::equalsIgnoreCase)) {
                AseUserPrincipal aseUserPrincipal = (AseUserPrincipal) authContext.getPrincipal();
                query.addCriteria(Criteria.where(Constants.NAME).is(aseUserPrincipal.getUser().getUsername()));
            }

            users = userService.findAll(query);

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') || hasAuthority('ROLE_DELIVERER') || hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<AseUser> getUser(@PathVariable("id") String id) {
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();

        // User and deliverer can only access their own user information
        String authority = authContext.getAuthorities().toString();
        if (Stream.of("[ROLE_DELIVERER]","[ROLE_CUSTOMER]").anyMatch(authority::equalsIgnoreCase)) {
            AseUserPrincipal aseUserPrincipal = (AseUserPrincipal) authContext.getPrincipal();
            id = aseUserPrincipal.getId();
        }

        Optional<AseUser> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DELIVERER') || hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<AseUser> updateUser(@PathVariable("id") String id, @RequestBody AseUser user) {
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();

        // User and deliverer can only access their own user information
        String authority = authContext.getAuthorities().toString();
        if (Stream.of("[ROLE_DELIVERER]","[ROLE_CUSTOMER]").anyMatch(authority::equalsIgnoreCase)) {
            AseUserPrincipal aseUserPrincipal = (AseUserPrincipal) authContext.getPrincipal();
            id = aseUserPrincipal.getId();
        }

        Optional<AseUser> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            AseUser _user = userOptional.get();
            if (!user.getName().isEmpty()){
                _user.setName(user.getName());
            }
            if (!user.getPassword().isEmpty()){
                _user.setPassword(user.getPassword());
            }
            if (!user.getRfidToken().isEmpty()){
                _user.setRfidToken(user.getRfidToken());
            }

            if (!user.getRole().toString().isEmpty() && Stream.of("[ROLE_DISPATCHER]").anyMatch(authority::equalsIgnoreCase)) {
                _user.setRole(user.getRole());
            }

            return new ResponseEntity<>(userService.save(_user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") String id) {
        try {
            userService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
