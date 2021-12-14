package edu.tum.ase.asedelivery.asedeliverymodels;

import com.mongodb.lang.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "users")
public class AseUser {
    // Define username, password and role properties
    @Id
    private String id;

    Collection<? extends GrantedAuthority>  authorities;

    @NonNull
    private String password;

    @Indexed(unique = true)
    @NonNull
    private String name;

    // Getters and Setters

    protected AseUser() {
    }

    public AseUser(String name, String password) {
        this.name = name;
        this.password = password;

    }


    public String getUsername() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isEnabled() {
        return true;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities= new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("test"));
        return authorities;
    }
}