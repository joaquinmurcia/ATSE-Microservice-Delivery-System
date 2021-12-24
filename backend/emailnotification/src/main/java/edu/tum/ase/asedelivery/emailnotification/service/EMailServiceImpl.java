package edu.tum.ase.asedelivery.emailnotification.service;

import edu.tum.ase.asedelivery.emailnotification.controller.EmailController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EMailServiceImpl{

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        //String text = String.format(emailController.templateSimpleMessage().getText());

        message.setFrom("noreply@aseDelivery.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        //emailController.getJavaMailSender().send(message);
        emailSender.send(message);
    }

}
