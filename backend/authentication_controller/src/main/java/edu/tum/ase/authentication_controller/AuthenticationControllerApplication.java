package edu.tum.ase.authentication_controller;

import com.mongodb.client.MongoClient;
import edu.tum.ase.authentication_controller.model.AseUser;
import edu.tum.ase.authentication_controller.model.Project;
import edu.tum.ase.authentication_controller.repository.ProjectRepository;
import edu.tum.ase.authentication_controller.repository.UserRepository;
import edu.tum.ase.authentication_controller.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.mongodb.client.MongoClient;

import java.util.List;


//@SpringBootApplication
//public class AuthenticationControllerApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(AuthenticationControllerApplication.class, args);
//	}
//
//}
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = {ProjectRepository.class})
public class AuthenticationControllerApplication implements CommandLineRunner {
	//...
	private static final Logger log = LoggerFactory.getLogger(AuthenticationControllerApplication.class);

	@Autowired
	MongoClient mongoClient;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	ProjectService projectService;


	public static void main(String[] args) {
		SpringApplication.run(AuthenticationControllerApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {

		log.info("MongoClient = " + mongoClient.getClusterDescription());
		// Only create test users when there is no user in DB
		if (userRepository.findAll().size() == 0) {
			// Create test users with hashed Bcrypt password and role
			userRepository.save(new AseUser("asdf",bCryptPasswordEncoder.encode("asdfasdf")));
		}

		log.info("MongoClient = " + mongoClient.getClusterDescription());

		String projectName = "ASE Delivery";

		Project project;

		if(projectService.findByName(projectName) == null)
			project = projectService.createProject(new Project(projectName));
		else
			project = projectService.findByName(projectName);

		log.info(String.format("Project %s is created with id %s",
				project.getName(),
				project.getId()));

		List<Project> projectList = projectService.getAllProjects();
		log.info("Number of Project in Database is " + projectList.size());

		Project aseDeliveryProject = projectService.findByName(projectName);

		log.info(String.format("Found Project %s with id %s",
				project.getName(),
				project.getId()));

		projectList = projectService.getAllProjects();
		log.info("Number of Project in Database is " + projectList.size());

	}
}