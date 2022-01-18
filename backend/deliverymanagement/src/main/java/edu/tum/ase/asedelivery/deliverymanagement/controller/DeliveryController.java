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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {
    // TODO Add role check not everybody should be allowed to request every delivery e.g. customers should only
    //  be allowed to request their own delivery whereas the dispatcher should be able to see all, same is true for
    //  updating and deleting deliveries

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

            for (Delivery delivery: deliveries) {
                // Checks if delivery status is open else return bad request
                // Delivery status for a new delivery always needs to be open
                if (delivery.getDeliveryStatus() != DeliveryStatus.open){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }

                // Create headers
                HttpHeaders headers = new HttpHeaders();
                headers.set("Cookie", cookie);

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

                // Update box status
                box.getBody().setBoxStatus(BoxStatus.occupied);
                ResponseEntity<Box> updated_box = restTemplate.exchange(String.format("http://localhost:9002/boxes/%s", delivery.getTargetBox()), HttpMethod.PUT, new HttpEntity<>(box.getBody(), headers), Box.class);
                if (!(Objects.requireNonNull(updated_box.getBody()).getBoxStatus() == BoxStatus.occupied)){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Set rfid token of users in delivery
                delivery.setResponsibleDelivererRfidToken(targetCustomer.getBody().getRfidToken());
                delivery.setResponsibleDelivererRfidToken(responsibleDeliverer.getBody().getRfidToken());
            }

            List<Delivery> _deliveries = deliveryService.saveAll(deliveries);
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
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<Delivery>> getDeliveries(@RequestBody Delivery payload) {
        try {
            List<Delivery> deliveries;
            Query query = new Query();

            // TODO Check permissions if user can perform query

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
            value = "/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<Delivery> getDelivery(@PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        // TODO Check permissions if user can perform query

        if (deliveryOptional.isPresent()) {
            return new ResponseEntity<>(deliveryOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT
    )
    public ResponseEntity<Delivery> updateDelivery(@PathVariable("id") String id, @RequestBody Delivery delivery) {
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

            // TODO Check if delivery status (set to open if not already open)
            // TODO Check if a box exists and is used
            // TODO Check if customer exists
            // TODO Check if driver exists

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<HttpStatus> deleteDelivery(@PathVariable("id") String id) {
        try {
            deliveryService.deleteById(id);

            // TODO Check permissions if user can perform query

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/{id}/pickup",
            method = RequestMethod.POST
    )
    public ResponseEntity<Delivery> pickupDelivery(@PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        // TODO Check permissions if delivery can deposit

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();
            _delivery.setDeliveryStatus(DeliveryStatus.pickedUp);

            //TODO Send notification?

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}/deposit",
            method = RequestMethod.POST
    )
    public ResponseEntity<Delivery> depositDelivery(@PathVariable("id") String id) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        // TODO Check permissions if deliverer can deposit

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();
            _delivery.setDeliveryStatus(DeliveryStatus.delivered);

            //TODO Send notification?

            return new ResponseEntity<>(deliveryService.save(_delivery), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}