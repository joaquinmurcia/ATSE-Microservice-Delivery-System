package edu.tum.ase.authentication_controller.service;

import edu.tum.ase.authentication_controller.model.Project;
import edu.tum.ase.authentication_controller.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project findByName(String name) {
        return projectRepository.findByName(name);
    }

    public List<Project> getAllProjects() {
        return new ArrayList<Project>(projectRepository.findAll());
    }
}
