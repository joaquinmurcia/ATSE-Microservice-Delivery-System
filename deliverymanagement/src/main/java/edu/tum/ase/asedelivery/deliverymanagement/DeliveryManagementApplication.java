package edu.tum.ase.asedelivery.deliverymanagement;

import com.mongodb.client.MongoClient;
import edu.tum.ase.asedelivery.deliverymanagement.repository.DeliveryRepository;
import edu.tum.ase.asedelivery.deliverymanagement.service.DeliveryService;
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
@EnableMongoRepositories(basePackageClasses = {DeliveryRepository.class})
public class DeliveryManagementApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DeliveryManagementApplication.class);

	@Autowired
	MongoClient mongoClient;

	@Autowired
	DeliveryService deliveryService;


	public static void main(String[] args) {
		SpringApplication.run(DeliveryManagementApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("MongoClient = " + mongoClient.getClusterDescription());

	}
}

