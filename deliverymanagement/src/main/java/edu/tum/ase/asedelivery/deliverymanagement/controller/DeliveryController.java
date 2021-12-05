package edu.tum.ase.asedelivery.deliverymanagement.controller;

import edu.tum.ase.asedelivery.deliverymanagement.model.Constants;
import edu.tum.ase.asedelivery.deliverymanagement.model.Delivery;
import edu.tum.ase.asedelivery.deliverymanagement.model.DeliveryStatus;
import edu.tum.ase.asedelivery.deliverymanagement.service.DeliveryService;
import edu.tum.ase.asedelivery.deliverymanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

            // TODO Check if a box is already used

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

            // Create query
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