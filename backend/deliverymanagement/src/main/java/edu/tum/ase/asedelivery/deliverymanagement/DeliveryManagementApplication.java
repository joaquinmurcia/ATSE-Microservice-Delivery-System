package edu.tum.ase.asedelivery.deliverymanagement;

import com.mongodb.client.MongoClient;
import edu.tum.ase.asedelivery.deliverymanagement.model.Delivery;
import edu.tum.ase.asedelivery.deliverymanagement.model.DeliveryStatus;
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

	@Autowired
	private DeliveryRepository deliveryRepository;

	public static void main(String[] args) {
		SpringApplication.run(DeliveryManagementApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("MongoClient = " + mongoClient.getClusterDescription());
		if (deliveryRepository.findAll().size() != 0) {
			deliveryRepository.deleteAll();
		}

		// Create list of dummy deliveries
		/*List<Delivery> deliveries = new ArrayList<Delivery>();
		deliveries.add(new Delivery("deliveryID1", "targetBox1", "targetCustomer1", "108560888149","deliverer1", "520413243569", DeliveryStatus.open));
		deliveries.add(new Delivery("deliveryID2", "targetBox1", "targetCustomer2", "108560888149","deliverer2", "rtoken1", DeliveryStatus.open));
*/

		List<Delivery> deliveries = new ArrayList<Delivery>();
		deliveries.add(new Delivery("deliveryTestID", "RBPIID1", "TestCustomer1", "RFIDToken1","TestDeliverer1", "RFIDToken2", DeliveryStatus.open));
		deliveries.add(new Delivery("deliveryTestID2", "RBPIID1", "TestCustomer1", "RFIDToken1","TestDeliverer2", "RFIDToken3", DeliveryStatus.open));

		List<Delivery> _deliveries = deliveryService.saveAll(deliveries);
	}
}

