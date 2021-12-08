package edu.tum.ase.authentication_controller.controller;

import edu.tum.ase.authentication_controller.model.Project;
import edu.tum.ase.authentication_controller.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('test')")
    public List<Project> getAllProject() {
        return projectService.getAllProjects();
    }

    // Implement an Endpoint to find a project with a given name
    @GetMapping("search/{projectName}")
    @ResponseBody
    public Project getProjectByName(@PathVariable String projectName) {
        return projectService.findByName(projectName);
    }

    // Implement a POST Endpoint to create a project with a given name
    @PostMapping("/new")
    @PreAuthorize("hasAuthority('Admin')")
    @ResponseBody
    public Project newProject(
            @RequestBody String projectName) {
        return projectService.createProject(new Project(projectName));
    }

}