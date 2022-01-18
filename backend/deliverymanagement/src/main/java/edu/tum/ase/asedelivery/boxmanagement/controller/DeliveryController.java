package edu.tum.ase.asedelivery.boxmanagement.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.*;
import edu.tum.ase.asedelivery.boxmanagement.service.DeliveryService;
import edu.tum.ase.asedelivery.boxmanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    public ResponseEntity<List<Delivery>> createDeliveries(@RequestBody List<Delivery> deliveries) {
        try {
            // Check if delivery has a valid format
            for (Delivery delivery : deliveries) {
                if (!delivery.isValid()){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }

            // TODO Check if delivery status (set to open if not already open)
            // TODO Check if a box exists and is used

            for (Delivery delivery: deliveries) {
                // Checks if delivery status is open else return bad request
                // Delivery status for a new delivery always needs to be open
                if (delivery.getDeliveryStatus() != DeliveryStatus.open){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }

                // Checks if customer exists
                AseUser targetCustomer = restTemplate.getForObject("lb://usermanagement/users/{id}", AseUser.class, delivery.getTargetCustomer());
                if (targetCustomer == null || targetCustomer.isEnabled()){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Checks if deliverer exists
                AseUser responsibleDeliverer = restTemplate.getForObject("lb://usermanagement/users/{id}", AseUser.class, delivery.getResponsibleDeliverer());
                if (responsibleDeliverer == null || responsibleDeliverer.isEnabled()){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Checks if a box exists
                Box box = restTemplate.getForObject("lb://boxmanagement/boxes/{id}", Box.class, delivery.getTargetBox());
                if (box == null || box.getBoxStatus() == BoxStatus.occupied){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                // Update box status
                box.setBoxStatus(BoxStatus.occupied);
                restTemplate.put(String.format("lb://boxmanagement/boxes/%s", box.getId()), box);

                // Set rfid token of users in delivery
                delivery.setResponsibleDelivererRfidToken(targetCustomer.getRfidToken());
                delivery.setResponsibleDelivererRfidToken(responsibleDeliverer.getRfidToken());
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

            // TODO Check permissions if user can perform query

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
            value = "/deliveries/{id}",
            method = RequestMethod.PUT
    )
    public ResponseEntity<Delivery> updateDelivery(@PathVariable("id") String id, @RequestBody Delivery delivery) {
        Optional<Delivery> deliveryOptional = deliveryService.findById(id);

        if (deliveryOptional.isPresent()) {
            Delivery _delivery = deliveryOptional.get();

            //These if statements ensure that the delivery status can only be changed in the right order
            //1. open -> 2. delivered -> 3. pickedup -> 1. open -> ...
            if (_delivery.getDeliveryStatus() == DeliveryStatus.open && delivery.getDeliveryStatus() == DeliveryStatus.pickedUp){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (_delivery.getDeliveryStatus() == DeliveryStatus.pickedUp && delivery.getDeliveryStatus() == DeliveryStatus.delivered){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (_delivery.getDeliveryStatus() == DeliveryStatus.delivered && delivery.getDeliveryStatus() == DeliveryStatus.open){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            if (!delivery.getDeliveryStatus().equals(_delivery.getDeliveryStatus())){
                _delivery.setDeliveryStatus(delivery.getDeliveryStatus());
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
            value = "/deliveries/{id}",
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
            value = "/deliveries/{id}/pickup",
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
            value = "/deliveries/{id}/deposit",
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