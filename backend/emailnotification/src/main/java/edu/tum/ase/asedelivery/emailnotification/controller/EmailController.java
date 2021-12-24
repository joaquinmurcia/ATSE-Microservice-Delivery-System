package edu.tum.ase.asedelivery.emailnotification.controller;

import edu.tum.ase.asedelivery.emailnotification.service.EMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    EMailServiceImpl emailService;

    @RequestMapping(
            value = "/deliveryCreated",
            method = RequestMethod.POST
    )
    public ResponseEntity<String> sendCreatedDeliveryEmail(@RequestBody String customerMail){
        if(!isEmailAdressValid(customerMail)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        String subject = "[ASE Delivery] New Delivery created";
        String text = "A new delivery was created for you";

        emailService.sendSimpleMessage(customerMail, subject, text);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/deliveryPlacedInBox",
            method = RequestMethod.POST
    )
    public ResponseEntity<String> sendDeliveryPlacedInBoxEmail(@RequestBody String customerMail){
        if(!isEmailAdressValid(customerMail)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        String subject = "[ASE Delivery] Delivery placed in Pickup-Box";
        String text = "Your delivery was placed in a pickup box for you";

        emailService.sendSimpleMessage(customerMail, subject, text);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/deliveriesPickedUp",
            method = RequestMethod.POST
    )
    public ResponseEntity<String> sendDeliveriesPickedUpEmail(@RequestBody String customerMail){
        if(!isEmailAdressValid(customerMail)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        String subject = "[ASE Delivery] Deliveries picked up";
        String text = "You picked up all your deliveries successfully";

        emailService.sendSimpleMessage(customerMail, subject, text);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    private boolean isEmailAdressValid(String email) {
        boolean emailValid = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email).find();
        return emailValid;
    }


}
