package edu.tum.ase.asedelivery.asedeliverymodels;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AseUserPrincipal implements UserDetails {
    private final String id;
    private final User user;

    public AseUserPrincipal(AseUser user) {
        this.id = user.getId();
        this.user = new User(user.getName(), user.getPassword(), user.isEnabled(), true, true, true, Collections.singleton(user.getRole()));
    }

    public String getId() {
        return this.id;
    }

    public User getUser(){
        return this.user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}