package edu.tum.ase.asedelivery.boxmanagement.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.AseUserDAO;
import edu.tum.ase.asedelivery.asedeliverymodels.Box;
import edu.tum.ase.asedelivery.asedeliverymodels.BoxStatus;
import edu.tum.ase.asedelivery.asedeliverymodels.UserRole;
import edu.tum.ase.asedelivery.boxmanagement.model.Constants;
import edu.tum.ase.asedelivery.boxmanagement.model.Delivery;
import edu.tum.ase.asedelivery.boxmanagement.model.DeliveryStatus;
import edu.tum.ase.asedelivery.boxmanagement.service.DeliveryService;
import edu.tum.ase.asedelivery.boxmanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
public class DeliveryController {
    // TODO Add role check not everybody should be allowed to request every delivery e.g. customers should only
    //  be allowed to request their own delivery whereas the dispatcher should be able to see all, same is true for
    //  updating and deleting deliveries

    @Autowired
    DeliveryService deliveryService;

    RestTemplate restTemplate;

    @RequestMapping(
            value = "/deliveries",
            method = RequestMethod.POST
    )
    public ResponseEntity<List<Delivery>> createDeliveries(@RequestHeader HttpHeaders header, @RequestBody List<Delivery> deliveries) {
        //Check authorization
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        try {
            // Check if delivery has a valid format
            for (Delivery delivery : deliveries) {
                if (!delivery.isValid()){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }

            for (Delivery delivery: deliveries) {
                //Checks if delivery status is open else return bad request
                //Delivery status for a new delivery always needs to be open
                if (delivery.getDeliveryStatus() != DeliveryStatus.open){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }

                //Checks if the box, customer and deliverer for a delivery is correct
                Box box = restTemplate.getForObject("http://boxmanagement/boxes/{id}", Box.class, delivery.getTargetBox());
                AseUserDAO customer = restTemplate.getForObject("http://usermngmt/users/{id}", AseUserDAO.class, delivery.getTargetCustomer());
                AseUserDAO deliverer = restTemplate.getForObject("http://usermngmt/users/{id}", AseUserDAO.class, delivery.getResponsibleDriver());

                if (box == null || box.getBoxStatus() == BoxStatus.occupied ||
                        customer == null || customer.getRole() != UserRole.ROLE_CUSTOMER ||
                        deliverer == null || deliverer.getRole() != UserRole.ROLE_DELIVERER) {
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }
            }

            List<Delivery> _deliveries = deliveryService.saveAll(deliveries);
            return new ResponseEntity<>(_deliveries, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/deliveries",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<Delivery>> getDeliveries(@RequestHeader HttpHeaders header, @RequestBody Delivery payload) {
        //Check authorization
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

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

            if (!Validation.isNullOrEmpty(payload.getResponsibleDriver())) {
                query.addCriteria(Criteria.where(Constants.RESPONSIBLE_DRIVER).is(payload.getResponsibleDriver()));
            }

            if (!Validation.isNullOrEmpty(payload.getDeliveryStatus())) {
                query.addCriteria(Criteria.where(Constants.DELIVERY_STATUS).is(payload.getDeliveryStatus()));
            }

            deliveries = deliveryService.findAll(query);

            if (deliveries.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(deliveries, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/deliveries/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<Delivery> getDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        AseUserDAO authenticatedRequester = getAuthenticatedUser(header);

        if (deliveryOptional.isPresent()) {
            //Only the delivery's customer and dispatchers are allowed to get deliveries
            if (authenticatedRequester.getName().equals(deliveryOptional.get().getTargetCustomer()) || hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) {
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
    public ResponseEntity<Delivery> updateDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id, @RequestBody Delivery delivery) {
        //Check authorization, status updates for dispatching and picking up are in own functions below
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            _delivery.setTargetBox(delivery.getTargetBox());
            _delivery.setTargetCustomer(delivery.getTargetCustomer());
            _delivery.setResponsibleDriver(delivery.getResponsibleDriver());
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
            if(!_delivery.getResponsibleDriver().equals(delivery.getResponsibleDriver())){
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
    public ResponseEntity<HttpStatus> deleteDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        //Check authorization
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

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
    public ResponseEntity<Delivery> pickupDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        AseUserDAO authenticatedRequester = getAuthenticatedUser(header);

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            //Only delivery's target customer can pickup a delivery
            if (!authenticatedRequester.getName().equals(_delivery.getTargetCustomer())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            _delivery.setDeliveryStatus(DeliveryStatus.pickedUp);

            //TODO Send notification?

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/deliveries/{id}/deposit",
            method = RequestMethod.POST
    )
    public ResponseEntity<Delivery> depositDelivery(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        AseUserDAO authenticatedRequester = getAuthenticatedUser(header);

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            //Only delivery's target customer can deposit a delivery
            if (!authenticatedRequester.getName().equals(_delivery.getResponsibleDriver())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            _delivery.setDeliveryStatus(DeliveryStatus.delivered);

            //TODO Send notification?

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private boolean hasRequesterCorrectRole(HttpHeaders header, UserRole neededUserRole){
        HttpEntity<Void> requestEntity = new HttpEntity<>(header);
        ResponseEntity<UserRole> response = restTemplate.exchange("http://usermngmt/auth/userRole", HttpMethod.GET, requestEntity, UserRole.class);
        UserRole authenticatedRequesterRole = response.getBody();

        if (authenticatedRequesterRole != neededUserRole){
            return false;
        } else {
            return true;
        }
    }

    private AseUserDAO getAuthenticatedUser(HttpHeaders header){
        HttpEntity<Void> requestEntity = new HttpEntity<>(header);
        ResponseEntity<AseUserDAO> response = restTemplate.exchange("http://usermngmt/auth/user", HttpMethod.GET, requestEntity, AseUserDAO.class);
        return response.getBody();
    }
}