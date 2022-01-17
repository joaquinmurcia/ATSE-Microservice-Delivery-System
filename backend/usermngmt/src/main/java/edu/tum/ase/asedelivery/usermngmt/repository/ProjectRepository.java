package edu.tum.ase.asedelivery.usermngmt.repository;

import edu.tum.ase.asedelivery.usermngmt.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
    Project findByName(String name);
}
