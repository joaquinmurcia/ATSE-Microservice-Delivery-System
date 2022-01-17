package edu.tum.ase.asedelivery.usermngmt.service;

import java.util.ArrayList;
import java.util.List;
import edu.tum.ase.asedelivery.asedeliverymodels.AseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.tum.ase.asedelivery.usermngmt.repository.UserRepository;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Query;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get data
    public List<AseUser> findAll(Query query) {
        return userRepository.findAll(query);
    }

    public Optional<AseUser> findById(String id) {
        return userRepository.findById(id);
    }

    // Store data
    public AseUser save(AseUser delivery) {
        return userRepository.save(delivery);
    }

    public List<AseUser> saveAll(List<AseUser> deliveries) {
        return userRepository.saveAll(deliveries);
    }

    // Remove Data
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public List<AseUser> getAllUsers() {
        return new ArrayList<AseUser>(userRepository.findAll());
    }
}