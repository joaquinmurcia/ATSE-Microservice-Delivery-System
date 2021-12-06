package edu.tum.ase.asedelivery.boxmanagement.controller;

import edu.tum.ase.asedelivery.boxmanagement.model.Constants;
import edu.tum.ase.asedelivery.boxmanagement.model.Box;
import edu.tum.ase.asedelivery.boxmanagement.model.BoxStatus;
import edu.tum.ase.asedelivery.boxmanagement.service.BoxService;
import edu.tum.ase.asedelivery.boxmanagement.utils.Validation;
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
public class BoxController {
    // TODO Add role check only dispatcher should be allowed to create new boxes

    @Autowired
    BoxService boxService;

    @RequestMapping(
            value = "/boxes",
            method = RequestMethod.POST
    )
    public ResponseEntity<List<Box>> createBoxes(@RequestBody List<Box> boxes) {
        try {
            // TODO Check if address is valid
            // TODO Check box status (newly created boxes should always be available)

            List<Box> _boxes = boxService.saveAll(boxes);
            return new ResponseEntity<>(_boxes, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/boxes",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<Box>> getBoxes(@RequestBody Box payload) {
        try {
            List<Box> boxes;
            Query query = new Query();

            // TODO Check permissions if user can perform query

            // Create query

            /*
            if (!Validation.isNullOrEmpty(payload.getAddress().toString())) {
                query.addCriteria(Criteria.where(Constants.ADDRESS_STREET_NAME).is(payload.getAddress().getStreetName()));
                query.addCriteria(Criteria.where(Constants.ADDRESS_STREET_NUMBER).is(payload.getAddress().getStreetName()));
                query.addCriteria(Criteria.where(Constants.ADDRESS_POSTCODE).is(payload.getAddress().getPostcode()));
                query.addCriteria(Criteria.where(Constants.ADDRESS_CITY).is(payload.getAddress().getCity()));
                query.addCriteria(Criteria.where(Constants.ADDRESS_COUNTRY).is(payload.getAddress().getCountry()));
            }
           */

            // Create query
            if (!Validation.isNullOrEmpty(payload.getBoxStatus())) {
                query.addCriteria(Criteria.where(Constants.BOX_STATUS).is(payload.getBoxStatus()));
            }

            if (!Validation.isNullOrEmpty(payload.getDeliveryID())) {
                query.addCriteria(Criteria.where(Constants.DELIVERY_ID).is(payload.getDeliveryID()));
            }

            boxes = boxService.findAll(query);

            if (boxes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(boxes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/boxes/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<Box> getBox(@PathVariable("id") String id) {
        Optional<Box> boxOptional = boxService.findById(id);

        // TODO Check permissions if user can perform query

        if (boxOptional.isPresent()) {
            return new ResponseEntity<>(boxOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/boxes/{id}",
            method = RequestMethod.PUT
    )
    public ResponseEntity<Box> updateBox(@PathVariable("id") String id, @RequestBody Box box) {
        Optional<Box> boxOptional = boxService.findById(id);

        if (boxOptional.isPresent()) {
            Box _box = boxOptional.get();
            _box.setAddress(box.getAddress());
            _box.setBoxStatus(box.getBoxStatus());
            _box.setDeliveryID(box.getDeliveryID());

            // TODO Check if address is valid
            // TODO Check box status

            return new ResponseEntity<>(boxService.save(_box), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/boxes/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<HttpStatus> deleteBox(@PathVariable("id") String id) {
        try {
            boxService.deleteById(id);

            // TODO Check permissions if user can perform query

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}