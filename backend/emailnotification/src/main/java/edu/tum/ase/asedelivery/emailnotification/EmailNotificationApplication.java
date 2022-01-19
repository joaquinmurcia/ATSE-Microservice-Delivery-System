package edu.tum.ase.asedelivery.emailnotification;

import edu.tum.ase.asedelivery.emailnotification.service.EMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import java.util.Properties;

@SpringBootApplication
public class EmailNotificationApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationApplication.class);

	@Autowired
	public EMailServiceImpl mailService;

	public static void main(String[] args) {
		SpringApplication.run(EmailNotificationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//mailService.sendSimpleMessage("enterMail here", "Test Mail", "ASE Delivery test Email");

		log.info("Send test EMail");
	}

}
