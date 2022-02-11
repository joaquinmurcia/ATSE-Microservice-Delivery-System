package edu.tum.ase.asedelivery.usermngmt.repository;

import edu.tum.ase.asedelivery.usermngmt.model.AseUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<AseUser, String>, UserRepositoryCustom {
    AseUser findByName(String name);
}
