package edu.tum.ase.asedelivery.boxmanagement.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.Box;
import edu.tum.ase.asedelivery.asedeliverymodels.BoxStatus;
import edu.tum.ase.asedelivery.boxmanagement.model.Constants;
import edu.tum.ase.asedelivery.boxmanagement.model.Delivery;
import edu.tum.ase.asedelivery.boxmanagement.model.DeliveryStatus;
import edu.tum.ase.asedelivery.boxmanagement.service.DeliveryService;
import edu.tum.ase.asedelivery.boxmanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
                //Checks if delivery status is open else return bad request
                //Delivery status for a new delivery always needs to be open
                if (delivery.getDeliveryStatus() != DeliveryStatus.open){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }

                //Checks if a box exists and isn't used else return a bad request
                //TODO delivery.getTargetBox() must be the id of the box
                Box box = restTemplate.getForObject("http://localhost:9002/boxes/{id}", Box.class, delivery.getTargetBox());
                if (box == null || box.getBoxStatus() == BoxStatus.occupied){
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }
            }

            // TODO Check if customer exists
            // TODO Check if driver exists

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