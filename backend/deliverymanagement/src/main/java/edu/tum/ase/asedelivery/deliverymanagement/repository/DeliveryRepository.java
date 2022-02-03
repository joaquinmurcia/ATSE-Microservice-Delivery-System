package edu.tum.ase.asedelivery.deliverymanagement.repository;

import edu.tum.ase.asedelivery.deliverymanagement.model.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeliveryRepository extends MongoRepository<Delivery, String>, DeliveryRepositoryCustom {

}