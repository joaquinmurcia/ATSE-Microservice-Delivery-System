package edu.tum.ase.asedelivery.usermngmt;

import java.util.Arrays;
import java.util.List;

import com.mongodb.client.MongoClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.tum.ase.asedelivery.usermngmt.model.AseUserDAO;
import edu.tum.ase.asedelivery.usermngmt.model.UserRole;
import edu.tum.ase.asedelivery.usermngmt.repository.UserRepository;
import edu.tum.ase.asedelivery.usermngmt.service.UserService;

// TODO: Look for all println and delete

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
		AseUserDAO[] users = {
				new AseUserDAO("User1", bCryptPasswordEncoder.encode("pwd1"), "RFIDToken1", UserRole.ROLE_CUSTOMER),
				new AseUserDAO("User2", bCryptPasswordEncoder.encode("pwd2"), "RFIDToken1", UserRole.ROLE_DELIVERER),
				new AseUserDAO("User3", bCryptPasswordEncoder.encode("pwd3"), "RFIDToken1",
						UserRole.ROLE_DISPATCHER), };
		List<AseUserDAO> usersList = Arrays.asList(users);
		userRepository.saveAll(usersList);

		List<AseUserDAO> retrievedUsersList = userService.getAllUsers();
		log.info("No. users: " + retrievedUsersList.size());
	}

}
