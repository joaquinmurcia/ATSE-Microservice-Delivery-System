package edu.tum.ase.asedelivery.boxmanagement.controller;

import edu.tum.ase.asedelivery.asedeliverymodels.UserRole;
import edu.tum.ase.asedelivery.boxmanagement.model.Address;
import edu.tum.ase.asedelivery.boxmanagement.model.Box;
import edu.tum.ase.asedelivery.boxmanagement.model.BoxStatus;
import edu.tum.ase.asedelivery.boxmanagement.model.Constants;
import edu.tum.ase.asedelivery.boxmanagement.service.BoxService;
import edu.tum.ase.asedelivery.boxmanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
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
    public ResponseEntity<List<Box>> createBoxes(@RequestHeader HttpHeaders header, @RequestBody List<Box> boxes) {
        //Check authorization
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

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
    public ResponseEntity<List<Box>> getBoxes(@RequestHeader HttpHeaders header, @RequestBody Box payload) {
        //Check authorization
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        try {
            List<Box> boxes;
            Query query = new Query();

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
    public ResponseEntity<Box> getBox(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        //Check authorization
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

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
    public ResponseEntity<Box> updateBox(@RequestHeader HttpHeaders header, @PathVariable("id") String id, @RequestBody Box box) {
        //Check authorization
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

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
    public ResponseEntity<HttpStatus> deleteBox(@RequestHeader HttpHeaders header, @PathVariable("id") String id) {
        //Check authorization
        if (!hasRequesterCorrectRole(header, UserRole.ROLE_DISPATCHER)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        try {
            boxService.deleteById(id);

            // TODO Check permissions if user can perform query

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
}