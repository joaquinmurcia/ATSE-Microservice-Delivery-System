package edu.tum.ase.asedelivery.deliverymanagement;

import com.mongodb.client.MongoClient;
import edu.tum.ase.asedelivery.asedeliverymodels.Address;
import edu.tum.ase.asedelivery.asedeliverymodels.Box;
import edu.tum.ase.asedelivery.asedeliverymodels.BoxStatus;
import edu.tum.ase.asedelivery.deliverymanagement.repository.BoxRepository;
import edu.tum.ase.asedelivery.deliverymanagement.service.BoxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;


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

		// Create list of dummy deliveries
		List<Box> boxes = new ArrayList<Box>();

		Address address1 = new Address("Straße", 1, 12345, "Stadt", "Land");
		Address address2 = new Address("Straße", 2, 12345, "Stadt", "Land");
		Address address3 = new Address("Straße", 3, 12345, "Stadt", "Land");

		boxes.add(new Box(null, address1, BoxStatus.available, "deliveryID1"));
		boxes.add(new Box(null, address1, BoxStatus.available, "deliveryID2"));
		boxes.add(new Box(null, address2, BoxStatus.occupied, "deliveryID3"));
		boxes.add(new Box(null, address2, BoxStatus.occupied, "deliveryID4"));
		boxes.add(new Box(null, address3, BoxStatus.occupied, "deliveryID5"));

		List<Box> _boxes = boxService.saveAll(boxes);

	}
}

