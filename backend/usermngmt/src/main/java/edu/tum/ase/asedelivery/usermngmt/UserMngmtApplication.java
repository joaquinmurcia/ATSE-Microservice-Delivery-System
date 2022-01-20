package edu.tum.ase.asedelivery.usermngmt;

import java.util.Arrays;
import java.util.List;
import com.mongodb.client.MongoClient;
import edu.tum.ase.asedelivery.asedeliverymodels.AseUser;
import edu.tum.ase.asedelivery.asedeliverymodels.UserRole;
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
		System.out.println("4aaaaa");
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
				new AseUser("User1", bCryptPasswordEncoder.encode("pwd1"), "RFIDToken1", UserRole.ROLE_CUSTOMER),
				new AseUser("TestCustomer1", "test@test.com","TestCustomer", bCryptPasswordEncoder.encode("pwd1"), "RFIDToken1", UserRole.ROLE_CUSTOMER),
				new AseUser("TestDeliverer1", null, "User2", bCryptPasswordEncoder.encode("pwd2"), "RFIDToken1", UserRole.ROLE_DELIVERER),
				new AseUser("User3", bCryptPasswordEncoder.encode("pwd3"), "RFIDToken1", UserRole.ROLE_DISPATCHER), };
		List<AseUser> usersList = Arrays.asList(users);
		userRepository.saveAll(usersList);

		List<AseUser> retrievedUsersList = userService.getAllUsers();
		log.info("No. users: " + retrievedUsersList.size());
	}

}
