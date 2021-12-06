package edu.tum.ase.asedelivery.boxmanagement;

import com.mongodb.client.MongoClient;
import edu.tum.ase.asedelivery.boxmanagement.repository.BoxRepository;
import edu.tum.ase.asedelivery.boxmanagement.service.BoxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@EnableDiscoveryClient
@EnableMongoRepositories(basePackageClasses = {BoxRepository.class})
public class BoxManagementApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(BoxManagementApplication.class);

	@Autowired
	MongoClient mongoClient;

	@Autowired
    BoxService boxService;

	public static void main(String[] args) {
		SpringApplication.run(BoxManagementApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("MongoClient = " + mongoClient.getClusterDescription());

	}
}

