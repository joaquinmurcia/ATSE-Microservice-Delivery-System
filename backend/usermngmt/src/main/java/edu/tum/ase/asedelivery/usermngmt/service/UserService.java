package edu.tum.ase.asedelivery.usermngmt.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tum.ase.asedelivery.usermngmt.model.AseUserDAO;
import edu.tum.ase.asedelivery.usermngmt.repository.UserRepository;

// TODO: test if this is the right name for the role (should I put only
// ROLE_DISPATCHER...?)
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /*
     * ?? I want to have "createDeliverer". If I create this, this class is
     * dependant from UserRole.
     * Should I then create DelivererService to implement that method?
     */
    public AseUserDAO createUser(AseUserDAO user) {
        return userRepository.save(user);
    }

    public void deleteUser(AseUserDAO user) {
        userRepository.delete(user);
        return;
    }

    public AseUserDAO findByName(String name) {
        return userRepository.findByName(name);
    }

    public List<AseUserDAO> getAllUsers() {
        // return userRepository.findAll();
        return new ArrayList<AseUserDAO>(userRepository.findAll());
    }
}
