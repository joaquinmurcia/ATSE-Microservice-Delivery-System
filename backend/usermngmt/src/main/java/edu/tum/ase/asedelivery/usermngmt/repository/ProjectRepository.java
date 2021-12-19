package edu.tum.ase.asedelivery.usermngmt.repository;


import edu.tum.ase.asedelivery.usermngmt.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface ProjectRepository extends MongoRepository<Project, String> {
    Project findByName(String name);
}
