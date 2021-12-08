package edu.tum.ase.authentication_controller.repository;

import edu.tum.ase.authentication_controller.model.AseUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface UserRepository extends MongoRepository<AseUser, String> {
        AseUser findByName(String name);
}

