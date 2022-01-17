package edu.tum.ase.asedelivery.boxmanagement.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.Address;
import edu.tum.ase.asedelivery.asedeliverymodels.Box;
import edu.tum.ase.asedelivery.asedeliverymodels.BoxStatus;
import edu.tum.ase.asedelivery.asedeliverymodels.Constants;
import edu.tum.ase.asedelivery.asedeliverymodels.UserRole;import edu.tum.ase.asedelivery.boxmanagement.service.BoxService;
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
import java.util.regex.Pattern;

@RestController
@RequestMapping("")
public class BoxController {
    // TODO Add role check only dispatcher should be allowed to create new boxes

    @Autowired
    BoxService boxService;

    RestTemplate restTemplate;

    @RequestMapping(
            value = "/boxes",
            method = RequestMethod.POST
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<Box>> createBoxes(@RequestHeader HttpHeaders header, @RequestBody List<Box> boxes) {
        try {
            for (Box box : boxes) {
                //Checks if box status is available, new boxes can only be available
                if (box.getBoxStatus() != BoxStatus.available) {
                    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }

                Address boxAdress = box.getAddress();
                if(!this.isAdressValid(boxAdress)){
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
            value = "/boxes",
            method = RequestMethod.GET
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<Box>> getBoxes(@RequestHeader HttpHeaders header, @RequestBody Box payload) {
        try {
            List<Box> boxes;
            Query query = new Query();

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
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Box> getBox(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        Optional<Box> boxOptional = boxService.findById(id);

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
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Box> updateBox(@RequestHeader HttpHeaders header, @PathVariable("id") String id, @RequestBody Box box) {
        Optional<Box> boxOptional = boxService.findById(id);

        if (boxOptional.isPresent()) {
            Box _box = boxOptional.get();
            _box.setAddress(box.getAddress());
            _box.setBoxStatus(box.getBoxStatus());
            _box.setDeliveryID(box.getDeliveryID());

            Address boxAdress = box.getAddress();
            if(!this.isAdressValid(boxAdress)){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(boxService.save(_box), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/boxes/{id}",
            method = RequestMethod.DELETE
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<HttpStatus> deleteBox(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        try {
            boxService.deleteById(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Checks if Street, City and Country of the box adress have a valid format
    private boolean isAdressValid(Address adress) {
        if (!Pattern.compile("[a-zA-Z]+").matcher(adress.getStreetName()).find()) {
            return false;
        }
        if (!Pattern.compile("[a-zA-Z]+").matcher(adress.getCity()).find()) {
            return false;
        }
        if (!Pattern.compile("[a-zA-Z]+").matcher(adress.getCountry()).find()) {
            return false;
        }

        return true;
    }
}