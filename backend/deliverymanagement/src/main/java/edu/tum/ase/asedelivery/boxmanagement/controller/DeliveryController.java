package edu.tum.ase.asedelivery.boxmanagement.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.*;
import edu.tum.ase.asedelivery.boxmanagement.service.DeliveryService;
import edu.tum.ase.asedelivery.boxmanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
public class DeliveryController {
    @Autowired
    DeliveryService deliveryService;

    RestTemplate restTemplate;

    @RequestMapping(
            value = "/deliveries",
            method = RequestMethod.POST
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<Delivery>> createDeliveries(@RequestHeader HttpHeaders header, @RequestBody List<Delivery> deliveries) {
        try {
            // Check if delivery has a valid format
            for (Delivery delivery : deliveries) {
                if (!delivery.isValid()){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }

            for (Delivery delivery: deliveries) {
                // Checks if delivery status is open else return bad request
                // Delivery status for a new delivery always needs to be open
                if (delivery.getDeliveryStatus() != DeliveryStatus.open) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }

                // Checks if customer exists
                AseUser targetCustomer = restTemplate.getForObject("lb://usermanagement/users/{id}", AseUser.class, delivery.getTargetCustomer());
                if (targetCustomer == null || targetCustomer.isEnabled()) {
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Checks if deliverer exists
                AseUser responsibleDeliverer = restTemplate.getForObject("lb://usermanagement/users/{id}", AseUser.class, delivery.getResponsibleDeliverer());
                if (responsibleDeliverer == null || responsibleDeliverer.isEnabled()) {
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Checks if a box exists
                Box box = restTemplate.getForObject("lb://boxmanagement/boxes/{id}", Box.class, delivery.getTargetBox());
                if (box == null || box.getBoxStatus() == BoxStatus.occupied) {
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Update box status
                box.setBoxStatus(BoxStatus.occupied);
                restTemplate.put(String.format("lb://boxmanagement/boxes/%s", box.getId()), box);

                // Set rfid token of users in delivery
                delivery.setResponsibleDelivererRfidToken(targetCustomer.getRfidToken());
                delivery.setResponsibleDelivererRfidToken(responsibleDeliverer.getRfidToken());
            }

            for (Delivery delivery: deliveries) {
                AseUserDAO customer = restTemplate.getForObject("http://usermngmt/users/{id}", AseUserDAO.class, delivery.getTargetCustomer());
                restTemplate.postForObject("http://emailnotification//deliveryCreated", customer.getEmail(), String.class);
            }

            List<Delivery> _deliveries = deliveryService.saveAll(deliveries);
            return new ResponseEntity<>(_deliveries, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/deliveries",
            method = RequestMethod.GET
    )
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<Delivery>> getDeliveries(@RequestBody Delivery payload) {
        try {
            List<Delivery> deliveries;
            Query query = new Query();

            // CrisNullOrEmptyeate query
            if (!Validation.isNullOrEmpty(payload.getTargetBox())) {
                query.addCriteria(Criteria.where(Constants.TARGET_BOX).is(payload.getTargetBox()));
            }

            if (!Validation.isNullOrEmpty(payload.getTargetCustomer())) {
                query.addCriteria(Criteria.where(Constants.TARGET_CUSTOMER).is(payload.getTargetCustomer()));
            }

            if (!Validation.isNullOrEmpty(payload.getResponsibleDeliverer())) {
                query.addCriteria(Criteria.where(Constants.RESPONSIBLE_DRIVER).is(payload.getResponsibleDeliverer()));
            }

            if (!Validation.isNullOrEmpty(payload.getDeliveryStatus())) {
                query.addCriteria(Criteria.where(Constants.DELIVERY_STATUS).is(payload.getDeliveryStatus()));
            }

            deliveries = deliveryService.findAll(query);
            System.out.println(deliveries.size() + " amount of deliveries");

            if (deliveries.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(deliveries, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("wtf");
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/deliveries/{id}",
            method = RequestMethod.GET
    )
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Delivery> getDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        //Calls authController because we need to get the user by the token
        HttpEntity<String> httpEntity = new HttpEntity<>("body", header);
        ResponseEntity<AseUser> responseRequestUser = restTemplate.exchange("http://usermngmt/user", HttpMethod.GET, httpEntity, AseUser.class);
        AseUser requestUser = responseRequestUser.getBody();

        if (deliveryOptional.isPresent()) {
            //Only the delivery's customer and dispatchers are allowed to get deliveries
            if (requestUser.getName().equals(deliveryOptional.get().getTargetCustomer()) || requestUser.getRole() == UserRole.ROLE_DISPATCHER) {
                return new ResponseEntity<>(deliveryOptional.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/deliveries/{id}",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Delivery> updateDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id, @RequestBody Delivery delivery) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            _delivery.setTargetBox(delivery.getTargetBox());
            _delivery.setTargetCustomer(delivery.getTargetCustomer());
            _delivery.setResponsibleDeliverer(delivery.getResponsibleDeliverer());
            _delivery.setDeliveryStatus(delivery.getDeliveryStatus());

            if (!delivery.isValid()){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            //Target customer of a delivery cant be changed
            if(!_delivery.getTargetCustomer().equals(delivery.getTargetCustomer())){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            //Target box of a delivery cant be changed
            if(!_delivery.getTargetBox().equals(delivery.getTargetBox())){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            //Responsible Driver of a delivery cant be changed
            if(!_delivery.getResponsibleDeliverer().equals(delivery.getResponsibleDeliverer())){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            //These if statements ensure that the delivery status can only be changed in the right order
            //1. open -> 2. pickedUp -> 3. delivered -> 1. open -> ...
            if (_delivery.getDeliveryStatus() == DeliveryStatus.open && delivery.getDeliveryStatus() == DeliveryStatus.delivered){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (_delivery.getDeliveryStatus() == DeliveryStatus.pickedUp && delivery.getDeliveryStatus() == DeliveryStatus.open){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (_delivery.getDeliveryStatus() == DeliveryStatus.delivered && delivery.getDeliveryStatus() == DeliveryStatus.pickedUp){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/deliveries/{id}",
            method = RequestMethod.DELETE
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<HttpStatus> deleteDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        try {
            deliveryService.deleteById(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/deliveries/{id}/pickup",
            method = RequestMethod.POST
    )
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Delivery> pickupDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        //Calls authController because we need to get the user by the token
        HttpEntity<String> httpEntity = new HttpEntity<>("body", header);
        ResponseEntity<AseUser> responseRequestUser = restTemplate.exchange("http://usermngmt/user", HttpMethod.GET, httpEntity, AseUser.class);
        AseUser requestUser = responseRequestUser.getBody();

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            //Only delivery's target customer can pickup a delivery
            if (!requestUser.getName().equals(_delivery.getTargetCustomer())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            _delivery.setDeliveryStatus(DeliveryStatus.pickedUp);
            restTemplate.postForObject("http://emailnotification//deliveriesPickedUp", requestUser.getEMail(), String.class);

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/deliveries/{id}/deposit",
            method = RequestMethod.POST
    )
    @PreAuthorize("hasAuthority('ROLE_DELIVERER')")
    public ResponseEntity<Delivery> depositDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        //Calls authController because we need to get the user by the token
        HttpEntity<String> httpEntity = new HttpEntity<>("body", header);
        ResponseEntity<AseUser> responseRequestUser = restTemplate.exchange("http://usermngmt/user", HttpMethod.GET, httpEntity, AseUser.class);
        AseUser requestUser = responseRequestUser.getBody();

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            //Only delivery's target customer can deposit a delivery
            if (!requestUser.getName().equals(_delivery.getResponsibleDriver())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            _delivery.setDeliveryStatus(DeliveryStatus.delivered);

            AseUser customer = new AseUser(_delivery.getTargetCustomer());
            customer = restTemplate.getForObject("http://usermngmt/users", AseUser.class, customer);

            restTemplate.postForObject("http://emailnotification//deliveryDeposited", customer.getMail(), String.class);

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}