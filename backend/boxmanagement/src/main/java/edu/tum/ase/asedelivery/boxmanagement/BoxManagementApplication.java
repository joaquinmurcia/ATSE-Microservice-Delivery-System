package edu.tum.ase.asedelivery.boxmanagement;

import com.mongodb.client.MongoClient;
import edu.tum.ase.asedelivery.boxmanagement.model.Address;
import edu.tum.ase.asedelivery.boxmanagement.model.Box;
import edu.tum.ase.asedelivery.boxmanagement.model.BoxStatus;
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

		/*List<String> deliveryIDs1 = new ArrayList<String>();
		deliveryIDs1.add("deliveryID1");

		List<String> deliveryIDs2 = new ArrayList<String>();
		deliveryIDs2.add("deliveryID2");

		List<String> deliveryIDs3 = new ArrayList<String>();
		deliveryIDs3.add("deliveryID3");

		List<String> deliveryIDs4 = new ArrayList<String>();
		deliveryIDs4.add("deliveryID4");

		List<String> deliveryIDs5 = new ArrayList<String>();
		deliveryIDs5.add("deliveryID5");

		boxes.add(new Box("targetBox1", address1, BoxStatus.available, deliveryIDs1, null));
		boxes.add(new Box(null, address1, BoxStatus.available, deliveryIDs2, null));
		boxes.add(new Box(null, address2, BoxStatus.available, deliveryIDs3, null));
		boxes.add(new Box(null, address2, BoxStatus.available, deliveryIDs4, null));
		boxes.add(new Box(null, address3, BoxStatus.available, deliveryIDs5, null));*/
		//boxes.add(new Box("targetBoxTest", address1, BoxStatus.available, new ArrayList<String>(), "asdf"));

		List<String> deliveries1 = new ArrayList<String>();
		deliveries1.add("deliveryTestID");
		boxes.add(new Box("RBPIID1", address1, BoxStatus.occupied, deliveries1, "RBPIID1"));
		boxes.add(new Box("RBPIID2", address2, BoxStatus.available, new ArrayList<String>(), "RBPIID2"));
		boxes.add(new Box("RBPIID3", address3, BoxStatus.available, new ArrayList<String>(), "RBPIID3"));

		List<Box> _boxes = boxService.saveAll(boxes);
	}
}

