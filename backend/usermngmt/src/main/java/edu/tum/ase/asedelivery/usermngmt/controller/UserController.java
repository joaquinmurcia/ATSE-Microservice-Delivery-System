package edu.tum.ase.asedelivery.usermngmt.controller;

import edu.tum.ase.asedelivery.usermngmt.jwt.JwtUtil;
import edu.tum.ase.asedelivery.usermngmt.model.AseUser;
import edu.tum.ase.asedelivery.usermngmt.model.AseUserPrincipal;
import edu.tum.ase.asedelivery.usermngmt.model.Constants;
import edu.tum.ase.asedelivery.usermngmt.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import edu.tum.ase.asedelivery.usermngmt.service.UserService;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    RestTemplate restTemplate = new RestTemplate();


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
                user.setEnabled(true);
                user.setId(user.getName());
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
    public ResponseEntity<List<AseUser>> getUsers( @RequestHeader("Cookie") String cookie) {
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<AseUser> users;
            Query query = new Query();

            int jwt_start = cookie.indexOf("jwt=");
            int jwt_end = cookie.indexOf(";",jwt_start);
            if (jwt_end == -1){
                jwt_end = cookie.length();
            }
            String jwt_string = cookie.substring(jwt_start + 4,jwt_end);

            String role = jwtUtil.getRole(jwt_string);
            String username = jwtUtil.extractUsername(jwt_string);

            /*if (payload.isPresent()) {
                if (!Validation.isNullOrEmpty(payload.get().getName())) {
                    query.addCriteria(Criteria.where(Constants.NAME).is(payload.get().getName()));
                }
                if (!Validation.isNullOrEmpty(payload.get().getRfidToken())) {
                    query.addCriteria(Criteria.where(Constants.RFID_TOKEN).is(payload.get().getRfidToken()));
                }
            }*/

            // User and deliverer can only access their own user information
            if ("ROLE_DELIVERER".equals(role) || "ROLE_CUSTOMER".equals(role)) {

                query.addCriteria(Criteria.where(Constants.NAME).is(username));
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
    public ResponseEntity<AseUser> getUser(@PathVariable("id") String id, @RequestHeader("Cookie") String cookie) {
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();

        int jwt_start = cookie.indexOf("jwt=");
        int jwt_end = cookie.indexOf(";",jwt_start);
        if (jwt_end == -1){
            jwt_end = cookie.length();
        }
        String jwt_string = cookie.substring(jwt_start + 4,jwt_end);

        String role = jwtUtil.getRole(jwt_string);
        String username = jwtUtil.extractUsername(jwt_string);

        // User and deliverer can only access their own user information
        if ("ROLE_DELIVERER".equals(role) || "ROLE_CUSTOMER".equals(role)) {
            id = username;
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
    public ResponseEntity<AseUser> updateUser(@PathVariable("id") String id, @RequestBody AseUser user,  @RequestHeader("Cookie") String cookie) {
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();

        int jwt_start = cookie.indexOf("jwt=");
        int jwt_end = cookie.indexOf(";",jwt_start);
        if (jwt_end == -1){
            jwt_end = cookie.length();
        }
        String jwt_string = cookie.substring(jwt_start + 4,jwt_end);

        String role = jwtUtil.getRole(jwt_string);
        String username = jwtUtil.extractUsername(jwt_string);


        // User and deliverer can only access their own user information
        if ("ROLE_DELIVERER".equals(role) || "ROLE_CUSTOMER".equals(role)) {
            id = username;
        }

        Optional<AseUser> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            AseUser _user = userOptional.get();
            if (!(user.getName() == null)){
                _user.setName(user.getName());
            }
            if (!(user.getPassword()== null)){
                _user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }
            if (!(user.getRfidToken()== null)){
                _user.setRfidToken(user.getRfidToken());
            }
            if (!(user.getEmail() == null)){
                _user.setEmail(user.getEmail());
            }

            if (!(user.getRole() == null) && "ROLE_DISPATCHER".equals(role)) {
                _user.setRole(user.getRole());
            }

            return new ResponseEntity<>(userService.save(_user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}/sendDepositMailtoCustomer",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER') || hasAuthority('ROLE_DELIVERER')")
    public ResponseEntity<AseUser> sendDepositMailtoCustomer(@PathVariable("id") String id, @RequestHeader("Cookie") String cookie) {
        Optional<AseUser> targetCustomer = userService.findById(id);

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);

        restTemplate.exchange("http://localhost:9005/email/deliveryDeposited", HttpMethod.POST, new HttpEntity<>(targetCustomer.get().getEmail(), headers), String.class);
        return new ResponseEntity<>(HttpStatus.OK);
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
