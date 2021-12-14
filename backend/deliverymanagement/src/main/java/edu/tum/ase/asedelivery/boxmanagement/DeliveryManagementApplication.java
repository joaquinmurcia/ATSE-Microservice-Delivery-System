package edu.tum.ase.asedelivery.boxmanagement;

import com.mongodb.client.MongoClient;
import edu.tum.ase.asedelivery.boxmanagement.model.Delivery;
import edu.tum.ase.asedelivery.boxmanagement.model.DeliveryStatus;
import edu.tum.ase.asedelivery.boxmanagement.repository.DeliveryRepository;
import edu.tum.ase.asedelivery.boxmanagement.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.*;


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

		// Create list of dummy deliveries
		List<Delivery> deliveries = new ArrayList<Delivery>();
		deliveries.add(new Delivery(null, "targetBox1", "targetCustomer1", "deliverer1", DeliveryStatus.open));
		deliveries.add(new Delivery(null, "targetBox2", "targetCustomer2", "deliverer2", DeliveryStatus.open));
		deliveries.add(new Delivery(null, "targetBox3", "targetCustomer3", "deliverer3", DeliveryStatus.pickedUp));
		deliveries.add(new Delivery(null, "targetBox4", "targetCustomer4", "deliverer4", DeliveryStatus.pickedUp));
		deliveries.add(new Delivery(null, "targetBox5", "targetCustomer5", "deliverer4", DeliveryStatus.pickedUp));
		deliveries.add(new Delivery(null, "targetBox6", "targetCustomer6", "deliverer5", DeliveryStatus.delivered));

		List<Delivery> _deliveries = deliveryService.saveAll(deliveries);
	}
}
