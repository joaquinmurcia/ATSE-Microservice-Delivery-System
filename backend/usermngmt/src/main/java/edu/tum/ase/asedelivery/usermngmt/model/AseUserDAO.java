package edu.tum.ase.asedelivery.usermngmt.model;

import com.mongodb.lang.NonNull;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class AseUserDAO {
    @Indexed(unique = true)
    @NonNull
    private String name;
    private String password;
    private String rFIDTokenString; // TODO: make AseUser interface and create subclasses. Dispatchers don't have
    // rFIDTokenString
    // TODO: Should the rFIDTokenString be null for dispatchers? Depends on
    // database. Maybe doesn't allow null.
    private UserRole role;
    private boolean isenabled;


    public AseUserDAO(String name, String password, String rFIDTokenString, UserRole role) {
        this.name = name;
        this.password = password;
        this.rFIDTokenString = rFIDTokenString;
        this.role = role;
        this.isenabled = true;
    }

    public String getrFIDTokenString() {
        return rFIDTokenString;
    }

    public void setrFIDTokenString(String rFIDTokenString) {
        this.rFIDTokenString = rFIDTokenString;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return this.role;
    }

    public boolean isEnabled() {
        return this.isenabled;
    }

    /*
     * TODO: (maybe) allow multiple roles for same user
     * public boolean addRole(UserRole role) {
     * return this.role.add(role);
     * }
     *
     * public boolean deleteRole(UserRole role) {
     * return this.role.remove(role);
     * }
     */
}
