package edu.tum.ase.asedelivery.usermngmt.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.tum.ase.asedelivery.usermngmt.model.AseUserDAO;

@Repository
public interface UserRepository extends MongoRepository<AseUserDAO, String>, UserRepositoryCustom {
    AseUserDAO findByName(String name);
}
