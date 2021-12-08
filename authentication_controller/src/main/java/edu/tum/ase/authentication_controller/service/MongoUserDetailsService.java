package edu.tum.ase.authentication_controller.service;

import edu.tum.ase.authentication_controller.AuthenticationControllerApplication;
import edu.tum.ase.authentication_controller.model.AseUser;
import edu.tum.ase.authentication_controller.model.AseUserPrincipal;
import edu.tum.ase.authentication_controller.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import edu.tum.ase.authentication_controller.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MongoUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationControllerApplication.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AseUser user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        log.info("user = " + user);

        // Return a Spring User with the
        // username, password and authority that we retrieved above
        return new AseUserPrincipal(user);
    }
}