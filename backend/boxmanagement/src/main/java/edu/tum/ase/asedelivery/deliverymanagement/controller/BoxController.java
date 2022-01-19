package edu.tum.ase.asedelivery.deliverymanagement.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.*;
import edu.tum.ase.asedelivery.deliverymanagement.service.BoxService;
import edu.tum.ase.asedelivery.deliverymanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/boxes")
public class BoxController {
    // TODO Add role check only dispatcher should be allowed to create new boxes

    @Autowired
    BoxService boxService;

    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<Box>> createBoxes(@RequestBody List<Box> boxes) {
        try {
            for (Box box : boxes) {
                if (box.getBoxStatus() != BoxStatus.available) {
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                Address boxAddress = box.getAddress();
                if(!this.isAddressValid(boxAddress)){
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }

            List<Box> _boxes = boxService.saveAll(boxes);
            return new ResponseEntity<>(_boxes, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.GET
    )
    @PreAuthorize(" hasAuthority('ROLE_DELIVERER') || hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<Box>> getBoxes(@RequestBody Optional<Box> payload) {
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<Box> boxes = new ArrayList<>();
            Query query = new Query();

            // Create query
            if (payload.isPresent()) {
                if (!Validation.isNullOrEmpty(payload.get().getBoxStatus())) {
                    query.addCriteria(Criteria.where(Constants.BOX_STATUS).is(payload.get().getBoxStatus()));
                }
                if (!Validation.isNullOrEmpty(payload.get().getDeliveryID())) {
                    query.addCriteria(Criteria.where(Constants.DELIVERY_ID).is(payload.get().getDeliveryID()));
                }

                boxes = boxService.findAll(query);
            }

            if (boxes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(boxes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    @PreAuthorize("hasAuthority('ROLE_DELIVERER') || hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Box> getBox(@PathVariable("id") String id) {
        Optional<Box> boxOptional = boxService.findById(id);

        if (boxOptional.isPresent()) {
            return new ResponseEntity<>(boxOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Box> updateBox(@PathVariable("id") String id, @RequestBody Optional<Box> box) {
        Optional<Box> boxOptional = boxService.findById(id);

        if (boxOptional.isPresent() && box.isPresent()) {
            Box _box = boxOptional.get();

            if (!box.get().getAddress().getStreetName().isEmpty()) {
                _box.getAddress().setStreetName(box.get().getAddress().getStreetName());
            }

            if (box.get().getAddress().getStreetNumber() != 0) {
                _box.getAddress().setStreetNumber(box.get().getAddress().getStreetNumber());
            }

            if (!box.get().getAddress().getCity().isEmpty()) {
                _box.getAddress().setCity(box.get().getAddress().getCity());
            }

            if (!box.get().getAddress().getCountry().isEmpty()) {
                _box.getAddress().setCountry(box.get().getAddress().getCountry());
            }

            if (box.get().getAddress().getPostcode() != 0) {
                _box.getAddress().setPostcode(box.get().getAddress().getPostcode());
            }

            if (!box.get().getBoxStatus().toString().isEmpty()) {
                _box.setBoxStatus(box.get().getBoxStatus());
            }

            if (!box.get().getDeliveryID().isEmpty()) {
                _box.setDeliveryID(box.get().getDeliveryID());
            }

            Address boxAddress = box.get().getAddress();
            if(!this.isAddressValid(boxAddress)){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(boxService.save(_box), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<HttpStatus> deleteBox(@PathVariable("id") String id) {
        try {
            boxService.deleteById(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Checks if street, city and country of the box address have a valid format
    private boolean isAddressValid(Address address) {
        if (!Pattern.compile("[a-zA-Z]+").matcher(address.getStreetName()).find()) {
            return false;
        }
        if (!Pattern.compile("[a-zA-Z]+").matcher(address.getCity()).find()) {
            return false;
        }
        return Pattern.compile("[a-zA-Z]+").matcher(address.getCountry()).find();
    }
}