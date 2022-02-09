package edu.tum.ase.asedelivery.usermngmt.service;

import edu.tum.ase.asedelivery.usermngmt.model.AseUser;
import edu.tum.ase.asedelivery.usermngmt.model.AseUserPrincipal;
import edu.tum.ase.asedelivery.usermngmt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MongoUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AseUser user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        // Return a Spring User with the
        // username, password and authority that we retrieved above
        return new AseUserPrincipal(user);
    }
}