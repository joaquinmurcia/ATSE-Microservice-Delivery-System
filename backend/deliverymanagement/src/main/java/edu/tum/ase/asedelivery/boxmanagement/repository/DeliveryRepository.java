package edu.tum.ase.asedelivery.boxmanagement.repository;

import edu.tum.ase.asedelivery.boxmanagement.model.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeliveryRepository extends MongoRepository<Delivery, String>, DeliveryRepositoryCustom {

}