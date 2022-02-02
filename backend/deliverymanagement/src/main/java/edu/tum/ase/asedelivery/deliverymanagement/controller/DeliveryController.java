package edu.tum.ase.asedelivery.deliverymanagement.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.*;
import edu.tum.ase.asedelivery.deliverymanagement.service.DeliveryService;
import edu.tum.ase.asedelivery.deliverymanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {
    @Autowired
    DeliveryService deliveryService;

    RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<Delivery>> createDeliveries(@RequestBody List<Delivery> deliveries, @RequestHeader("Cookie") String cookie) {
        try {
            // Check if delivery has a valid format
            for (Delivery delivery : deliveries) {
                if (!delivery.isValid()){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            for (Delivery delivery: deliveries) {
                // Checks if delivery status is open else return bad request
                // Delivery status for a new delivery always needs to be open
                if (delivery.getDeliveryStatus() != DeliveryStatus.open) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }

                // Checks if customer exists
                ResponseEntity<AseUser> targetCustomer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", delivery.getTargetCustomer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
                if (!Objects.requireNonNull(targetCustomer.getBody()).isEnabled()){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Checks if deliverer exists
                ResponseEntity<AseUser> responsibleDeliverer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", delivery.getResponsibleDeliverer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
                if (!Objects.requireNonNull(responsibleDeliverer.getBody()).isEnabled()){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Checks if a box exists
                ResponseEntity<Box> box = restTemplate.exchange(String.format("http://localhost:9002/boxes/%s", delivery.getTargetBox()), HttpMethod.GET, new HttpEntity<>(headers), Box.class);
                if (Objects.requireNonNull(box.getBody()).getBoxStatus() == BoxStatus.occupied){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Set rfid token of users in delivery
                delivery.setResponsibleDelivererRfidToken(targetCustomer.getBody().getRfidToken());
                delivery.setResponsibleDelivererRfidToken(responsibleDeliverer.getBody().getRfidToken());
            }

            for (Delivery delivery: deliveries) {
                ResponseEntity<AseUser> customer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", delivery.getTargetCustomer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
                if (!Objects.requireNonNull(customer.getBody()).isEnabled()){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                restTemplate.postForObject("http://localhost:9005/email/deliveryCreated", customer.getBody().getEmail(), String.class);
            }

            List<Delivery> _deliveries = deliveryService.saveAll(deliveries);

            for (Delivery delivery: deliveries) {
                // Get Box of delivery
                ResponseEntity<Box> box = restTemplate.exchange(String.format("http://localhost:9002/boxes/%s", delivery.getTargetBox()), HttpMethod.GET, new HttpEntity<>(headers), Box.class);
                if (Objects.requireNonNull(box.getBody()).getBoxStatus() == BoxStatus.occupied){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // add delivery to box
                box.getBody().setBoxStatus(BoxStatus.occupied);
                ResponseEntity<Box> httpResponse = restTemplate.exchange(String.format("http://localhost:9002/boxes/%s/addDelivery/%s", delivery.getTargetBox(), delivery.getId()), HttpMethod.PUT, new HttpEntity<>(box.getBody(), headers), Box.class);
                if (!(httpResponse.getStatusCode() == HttpStatus.OK)){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }
            }

            return new ResponseEntity<>(_deliveries, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.GET
    )
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') || hasAuthority('ROLE_DELIVERER') || hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<Delivery>> getDeliveries(@RequestParam(required = false) String boxId, @RequestParam(required = false) String customerId, @RequestParam(required = false) String delivererId, @RequestParam(required = false) DeliveryStatus deliveryStatus, @RequestHeader("Cookie") String cookie) {
        try {
            List<Delivery> deliveries;
            Query query = new Query();

            if (!Validation.isNullOrEmpty(boxId)) {
                query.addCriteria(Criteria.where(Constants.TARGET_BOX).is(boxId));
            }

            if (!Validation.isNullOrEmpty(customerId)) {
                query.addCriteria(Criteria.where(Constants.TARGET_CUSTOMER).is(customerId));
            }

            if (!Validation.isNullOrEmpty(delivererId)) {
                query.addCriteria(Criteria.where(Constants.RESPONSIBLE_DRIVER).is(delivererId));
            }

            if (!Validation.isNullOrEmpty(deliveryStatus)) {
                query.addCriteria(Criteria.where(Constants.DELIVERY_STATUS).is(deliveryStatus));
            }

            deliveries = deliveryService.findAll(query);
            System.out.println(deliveries.size() + " amount of deliveries");

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            //Checks authorization customers and deliverers are only allowed to get their own deliveries
            for (Delivery delivery: deliveries) {
                // Checks if requester is the customer of the delivery or a dispatcher
                ResponseEntity<AseUser> targetCustomer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", delivery.getTargetCustomer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
                // Checks if requester is the deliverer of the delivery or a dispatcher
                ResponseEntity<AseUser> responsibleDriver = restTemplate.exchange(String.format("http://localhost:9004/users/%s", delivery.getResponsibleDeliverer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);

                if (!Objects.requireNonNull(targetCustomer.getBody()).isEnabled() && !Objects.requireNonNull(responsibleDriver.getBody()).isEnabled()){
                    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                }
            }

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
            value = "/{id}",
            method = RequestMethod.GET
    )
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') || hasAuthority('ROLE_DELIVERER') || hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Delivery> getDelivery(@PathVariable("id") String id,  @RequestHeader("Cookie") String cookie) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        if (deliveryOptional.isPresent()) {
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            // Checks if requester is the customer of the delivery or a dispatcher
            ResponseEntity<AseUser> targetCustomer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", deliveryOptional.get().getTargetCustomer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
            // Checks if requester is the deliverer of the delivery or a dispatcher
            ResponseEntity<AseUser> responsibleDriver = restTemplate.exchange(String.format("http://localhost:9004/users/%s", deliveryOptional.get().getResponsibleDeliverer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);

            if (!Objects.requireNonNull(targetCustomer.getBody()).isEnabled() && !Objects.requireNonNull(responsibleDriver.getBody()).isEnabled()){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(deliveryOptional.get(), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Delivery> updateDelivery(@PathVariable("id") String id, @RequestBody Delivery delivery, @RequestHeader("Cookie") String cookie) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        if (deliveryOptional.isPresent()) {
            Delivery oldDelivery = deliveryOptional.get();
            Delivery updatedDelivery = oldDelivery.copyWith(delivery);

            //Checks if id was changed
            if(oldDelivery.getId() != updatedDelivery.getId()){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            //These if statements ensure that the delivery status can only be changed in the right order
            //1. open -> 2. collected -> 3. delivered -> 4. pickedup -> 1. open -> ...
            if (oldDelivery.getDeliveryStatus() == DeliveryStatus.open && (updatedDelivery.getDeliveryStatus() == DeliveryStatus.pickedUp || updatedDelivery.getDeliveryStatus() == DeliveryStatus.delivered)){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (oldDelivery.getDeliveryStatus() == DeliveryStatus.collected && (updatedDelivery.getDeliveryStatus() == DeliveryStatus.pickedUp || updatedDelivery.getDeliveryStatus() == DeliveryStatus.open)){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (oldDelivery.getDeliveryStatus() == DeliveryStatus.pickedUp && (updatedDelivery.getDeliveryStatus() == DeliveryStatus.collected || updatedDelivery.getDeliveryStatus() == DeliveryStatus.delivered)){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (oldDelivery.getDeliveryStatus() == DeliveryStatus.delivered && (updatedDelivery.getDeliveryStatus() == DeliveryStatus.open || updatedDelivery.getDeliveryStatus() == DeliveryStatus.collected)){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            // Checks if a box exists
            ResponseEntity<Box> box = restTemplate.exchange(String.format("http://localhost:9002/boxes/%s", updatedDelivery.getTargetBox()), HttpMethod.GET, new HttpEntity<>(headers), Box.class);
            if (Objects.requireNonNull(box.getBody()).getBoxStatus() == BoxStatus.occupied){
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }

            // Checks if customer exists
            ResponseEntity<AseUser> targetCustomer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", updatedDelivery.getTargetCustomer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
            if (!Objects.requireNonNull(targetCustomer.getBody()).isEnabled()){
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }

            // Checks if deliverer exists
            ResponseEntity<AseUser> responsibleDeliverer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", updatedDelivery.getResponsibleDeliverer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
            if (!Objects.requireNonNull(responsibleDeliverer.getBody()).isEnabled()){
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }

            return new ResponseEntity<>(deliveryService.save(updatedDelivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<HttpStatus> deleteDelivery(@PathVariable("id") String id) {
        try {
            deliveryService.deleteById(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "collectDeliveries",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DELIVERER')")
    public ResponseEntity<List<Delivery>> collectDeliveries(@RequestBody List<Delivery> deliveries, @RequestHeader("Cookie") String cookie) {
        try {
            List<Delivery> collectedDeliveries = new ArrayList<Delivery>();

            for (Delivery delivery : deliveries){
                Optional <Delivery> _delivery = deliveryService.findById(delivery.getId());

                if (_delivery.isPresent()) {
                    // Create headers
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Cookie", cookie);

                    // Checks if requester is the deliverer of the delivery
                    ResponseEntity<AseUser> responsibleDeliverer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", _delivery.get().getResponsibleDeliverer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
                    if (!Objects.requireNonNull(responsibleDeliverer.getBody()).isEnabled()){
                        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                    }

                    collectedDeliveries.add(_delivery.get());
                } else {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
            }

            for (Delivery delivery: collectedDeliveries){
                delivery.setDeliveryStatus(DeliveryStatus.pickedUp);
            }

            return new ResponseEntity<>(collectedDeliveries, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/{id}/deposit",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Delivery> depositDelivery(@PathVariable("id") String id, @RequestHeader("Cookie") String cookie) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            // Get targetCustomer to use the customers Email
            ResponseEntity<AseUser> targetCustomer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", _delivery.getTargetCustomer()), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
            if (!Objects.requireNonNull(targetCustomer.getBody()).isEnabled()){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }

            _delivery.setDeliveryStatus(DeliveryStatus.delivered);
            restTemplate.exchange("http://localhost:9005/email/deliveryDeposited", HttpMethod.POST, new HttpEntity<>(targetCustomer.getBody().getEmail(), headers), String.class);

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/pickup/{id}",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<HttpStatus> pickupDelivery(@PathVariable("id") String id, @RequestHeader("Cookie") String cookie) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            _delivery.setDeliveryStatus(DeliveryStatus.pickedUp);

            deliveryService.save(_delivery);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}