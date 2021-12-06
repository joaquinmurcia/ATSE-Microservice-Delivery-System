package edu.tum.ase.asedelivery.boxmanagement.repository;

import edu.tum.ase.asedelivery.boxmanagement.model.Box;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoxRepository extends MongoRepository<Box, String>, BoxRepositoryCustom {

}