package edu.tum.ase.authentication_controller.repository;


import edu.tum.ase.authentication_controller.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    Project findByName(String name);
}
