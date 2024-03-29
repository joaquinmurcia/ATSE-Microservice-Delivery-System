package edu.tum.ase.asedelivery.usermngmt;

import java.util.Arrays;
import java.util.List;
import com.mongodb.client.MongoClient;
import edu.tum.ase.asedelivery.usermngmt.model.AseUser;
import edu.tum.ase.asedelivery.usermngmt.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import edu.tum.ase.asedelivery.usermngmt.repository.UserRepository;
import edu.tum.ase.asedelivery.usermngmt.service.UserService;

// TODO: Look for all println and delete

//!If problems, maybe this works
/*@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@SpringBootApplication(exclude = {
		MongoDataAutoConfiguration.class
})*/
//!
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = { UserRepository.class })
public class UserMngmtApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(UserMngmtApplication.class);

	@Autowired
	MongoClient mongoClient;

	@Autowired
	UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(UserMngmtApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("MongoClient = " + mongoClient.getClusterDescription());

		// DEBUG: Setup initial users
		if (userRepository.findAll().size() != 0) {
			userRepository.deleteAll();
		}
		// Create test users with hashed Bcrypt password and role
		AseUser[] users = {
				new AseUser("TestCustomer1", "costumer1@ase.de","TestCustomer1", bCryptPasswordEncoder.encode("pwd1"), "108560888149", UserRole.ROLE_CUSTOMER),
				new AseUser("TestCustomer2", "costumer2@ase.de","TestCustomer2", bCryptPasswordEncoder.encode("pwd2"), "RFIDToken2", UserRole.ROLE_CUSTOMER),
				new AseUser("TestCustomer3", "costumer3@ase.de","TestCustomer3", bCryptPasswordEncoder.encode("pwd3"), "RFIDToken3", UserRole.ROLE_CUSTOMER),
				new AseUser("TestDeliverer1", "deliverer1@ase.de", "TestDeliverer1", bCryptPasswordEncoder.encode("pwd1"), "520413243569", UserRole.ROLE_DELIVERER),
				new AseUser("TestDeliverer2", "deliverer2@ase.de", "TestDeliverer2", bCryptPasswordEncoder.encode("pwd2"), "RFIDToken4", UserRole.ROLE_DELIVERER),
				new AseUser("Dispatcher", "dispatcher@ase.de", "Dispatcher", bCryptPasswordEncoder.encode("pwd3"), "RFIDToken5", UserRole.ROLE_DISPATCHER),
		};
		List<AseUser> usersList = Arrays.asList(users);
		userRepository.saveAll(usersList);

		List<AseUser> retrievedUsersList = userService.getAllUsers();
		log.info("No. users: " + retrievedUsersList.size());
	}

}
