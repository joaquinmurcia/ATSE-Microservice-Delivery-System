package edu.tum.ase.asedelivery.boxmanagement.controller;

import edu.tum.ase.asedelivery.boxmanagement.model.*;
import edu.tum.ase.asedelivery.boxmanagement.service.BoxService;
import edu.tum.ase.asedelivery.boxmanagement.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/boxes")
public class BoxController {
    // TODO Add role check only dispatcher should be allowed to create new boxes

    @Autowired
    BoxService boxService;

    RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<List<Box>> createBoxes(@RequestBody List<Box> boxes) {
        try {
            for (Box box : boxes) {
                box.setId(box.getRaspberryPiID());
                if (box.getBoxStatus() != BoxStatus.available || box.getRaspberryPiID() == null) {
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
    @PreAuthorize(" hasAuthority('ROLE_DELIVERER') || hasAuthority('ROLE_DISPATCHER') || hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<Box>> getBoxes(@RequestParam(required = false) String customerId, @RequestParam(required = false) List<String> deliveryIds, @RequestParam(required = false) BoxStatus boxStatus, @RequestHeader("Cookie") String cookie) {
        Authentication authContext = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<Box> boxes = new ArrayList<>();
            Query query = new Query();

            // Create query

            if (!Validation.isNullOrEmpty(boxStatus)) {
                query.addCriteria(Criteria.where(Constants.BOX_STATUS).is(boxStatus));
            }
            if (!Validation.isNullOrEmpty(deliveryIds)) {
                query.addCriteria(Criteria.where(Constants.DELIVERY_ID+"s").is(deliveryIds));
            }

            boxes = boxService.findAll(query);

            if (!Validation.isNullOrEmpty(customerId)) {
                // Create headers
                HttpHeaders headers = new HttpHeaders();
                headers.set("Cookie", cookie);
                List<Box> customerBoxes = new ArrayList<>();

                for (Box box : boxes)
                {
                    if (box.getDeliveryIDs() != null){
                        if(box.getDeliveryIDs().size() != 0) {
                            ResponseEntity<Delivery> delivery = null;
                            try {
                                delivery = restTemplate.exchange(String.format("http://localhost:9003/deliveries/%s", box.getDeliveryIDs().get(0)), HttpMethod.GET, new HttpEntity<>(headers), Delivery.class);
                            }catch (Exception e){
                                continue;
                            }
                            if (delivery.getStatusCodeValue() == 200) {
                                customerBoxes.add(box);
                            }
                        }
                    }
                }
                boxes = customerBoxes;
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
    public ResponseEntity<Box> updateBox(@PathVariable("id") String id, @RequestBody Box box) {
        Optional<Box> boxOptional = boxService.findById(id);

        if (boxOptional.isPresent()) {
            Box oldBox = boxOptional.get();
            Box updatedBox = oldBox.copyWith(box);

            if(!oldBox.getId().equals(updatedBox.getId())){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            Address boxAddress = updatedBox.getAddress();
            if(!this.isAddressValid(boxAddress)){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(boxService.save(updatedBox), HttpStatus.OK);
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

    @RequestMapping(
            value = "{id}/addDelivery/{deliveryID}",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Box> addDelivery(@PathVariable("id") String id, @PathVariable("deliveryID") String deliveryID, @RequestHeader("Cookie") String cookie) {
        try {
            Optional<Box> boxOptional = boxService.findById(id);

            if(boxOptional.isPresent()) {
                Box _box = boxOptional.get();
                _box.setBoxStatus(BoxStatus.occupied);

                if (_box.getDeliveryIDs() == null || _box.getDeliveryIDs().size() == 0){
                    List<String> deliveryIDs = new ArrayList<String>();
                    deliveryIDs.add(deliveryID);
                    _box.setDeliveryIDs(deliveryIDs);
                } else {
                    List<String> deliveryIDs = _box.getDeliveryIDs();
                    String firstDeliveryID = deliveryIDs.get(0);

                    // Create headers
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Cookie", cookie);

                    // Checks if delivery customer is the same customer as the already existing delivieries
                    ResponseEntity<Delivery> firstDelivery = restTemplate.exchange(String.format("http://localhost:9003/deliveries/%s", firstDeliveryID), HttpMethod.GET, new HttpEntity<>(headers), Delivery.class);
                    ResponseEntity<Delivery> toBeAddedDelivery = restTemplate.exchange(String.format("http://localhost:9003/deliveries/%s", deliveryID), HttpMethod.GET, new HttpEntity<>(headers), Delivery.class);

                    //If Customer check is valid add delivery to box
                    if(Objects.requireNonNull(firstDelivery.getBody()).getTargetCustomer().equals(Objects.requireNonNull(toBeAddedDelivery.getBody()).getTargetCustomer())) {
                        deliveryIDs.add(toBeAddedDelivery.getBody().getId());
                        _box.setDeliveryIDs(deliveryIDs);
                    } else {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }
                boxService.save(_box);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "{id}/pickupDeliveries",
            method = RequestMethod.PUT
    )
    @PreAuthorize("hasAuthority('ROLE_DISPATCHER')")
    public ResponseEntity<Box> pickupDeliveries(@PathVariable("id") String id, @RequestHeader("Cookie") String cookie) {
        try {
            Optional<Box> boxOptional = boxService.findById(id);

            if(boxOptional.isPresent()) {
                Box _box = boxOptional.get();
                String customerID = null;

                // Create headers
                HttpHeaders headers = new HttpHeaders();
                headers.set("Cookie", cookie);

                List<String> deliveriesPickedUp = new ArrayList<String>();

                for (String deliveryID : _box.getDeliveryIDs()){

                    ResponseEntity<Delivery> delivery = restTemplate.exchange(String.format("http://localhost:9003/deliveries/%s", deliveryID), HttpMethod.GET, new HttpEntity<>(headers), Delivery.class);
                    customerID = delivery.getBody().getTargetCustomer();

                    //Check if delivery was placed in box
                    if (Objects.requireNonNull(delivery.getBody()).getDeliveryStatus() == DeliveryStatus.delivered) {
                        deliveriesPickedUp.add(deliveryID);
                    }
                }

                for(String deliveryIDPickedUp: deliveriesPickedUp) {
                    ResponseEntity<HttpStatus> httpResponse = restTemplate.exchange(String.format("http://localhost:9003/deliveries/pickup/%s", deliveryIDPickedUp), HttpMethod.PUT, new HttpEntity<>(headers), HttpStatus.class);
                    if(httpResponse.getStatusCode() != HttpStatus.OK){
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                    _box.getDeliveryIDs().remove(deliveryIDPickedUp);
                }

                //If all deliveries are picked-up set the box status to available and send mail to customer
                if(Validation.isNullOrEmpty(_box.getDeliveryIDs()) && customerID != null) {
                    _box.setBoxStatus(BoxStatus.available);

                    // Get targetCustomer to use the customers Email
                    ResponseEntity<AseUser> targetCustomer = restTemplate.exchange(String.format("http://localhost:9004/users/%s", customerID), HttpMethod.GET, new HttpEntity<>(headers), AseUser.class);
                    if (!Objects.requireNonNull(targetCustomer.getBody()).isEnabled()){
                        return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                    }

                    restTemplate.exchange("http://localhost:9005/email/deliveriesPickedUp", HttpMethod.POST, new HttpEntity<>(targetCustomer.getBody().getEmail(), headers), String.class);
                }

                boxService.save(_box);

                return new ResponseEntity<>(HttpStatus.OK);
            }
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