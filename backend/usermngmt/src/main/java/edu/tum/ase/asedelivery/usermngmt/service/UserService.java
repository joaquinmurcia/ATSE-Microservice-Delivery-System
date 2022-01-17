package edu.tum.ase.asedelivery.usermngmt.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.tum.ase.asedelivery.usermngmt.model.AseUserDAO;
import edu.tum.ase.asedelivery.usermngmt.repository.UserRepository;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Query;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get data
    public List<AseUserDAO> findAll(Query query) {
        return userRepository.findAll(query);
    }

    public Optional<AseUserDAO> findById(String id) {
        return userRepository.findById(id);
    }

    // Store data
    public AseUserDAO save(AseUserDAO delivery) {
        return userRepository.save(delivery);
    }

    public List<AseUserDAO> saveAll(List<AseUserDAO> deliveries) {
        return userRepository.saveAll(deliveries);
    }

    // Remove Data
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public List<AseUserDAO> getAllUsers() {
        return new ArrayList<AseUserDAO>(userRepository.findAll());
    }
}