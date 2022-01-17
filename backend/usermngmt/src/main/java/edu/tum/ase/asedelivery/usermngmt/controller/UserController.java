package edu.tum.ase.asedelivery.usermngmt.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.AseUser;
import edu.tum.ase.asedelivery.asedeliverymodels.Constants;
import edu.tum.ase.asedelivery.usermngmt.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import edu.tum.ase.asedelivery.usermngmt.service.UserService;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
public class UserController {

    @Autowired
    UserService userService;

    RestTemplate restTemplate;

    @RequestMapping(
            value = "/users",
            method = RequestMethod.POST
    )
    public ResponseEntity<List<AseUser>> createUsers(@RequestBody List<AseUser> users) {
        try {
            // TODO Validate users

            List<AseUser> _users = userService.saveAll(users);
            return new ResponseEntity<>(_users, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/users",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<AseUser>> getUsers(@RequestBody AseUser payload) {
        try {
            List<AseUser> users;
            Query query = new Query();

            // TODO Check permissions if user can perform query

            if (!Validation.isNullOrEmpty(payload.getName())) {
                query.addCriteria(Criteria.where(Constants.NAME).is(payload.getName()));
            }

            if (!Validation.isNullOrEmpty(payload.getRfidToken())) {
                query.addCriteria(Criteria.where(Constants.RFID_TOKEN).is(payload.getRfidToken()));
            }

            users = userService.findAll(query);

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/users/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<AseUser> getUser(@PathVariable("id") String id) {
        Optional<AseUser> userOptional = userService.findById(id);

        // TODO Check permissions if user can perform query

        if (userOptional.isPresent()) {
            return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/users/{id}",
            method = RequestMethod.PUT
    )
    public ResponseEntity<AseUser> updateUser(@PathVariable("id") String id, @RequestBody AseUser user) {
        Optional<AseUser> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            AseUser _user = userOptional.get();
            _user.setName(user.getName());
            _user.setPassword(user.getPassword());
            _user.setRfidToken(user.getRfidToken());
            _user.setRole(user.getRole());

            // TODO Check if user values

            return new ResponseEntity<>(userService.save(_user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/users/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") String id) {
        try {
            userService.deleteById(id);

            // TODO Check permissions if user can perform query

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
