package edu.tum.ase.asedelivery.deliverymanagement.model;

import com.mongodb.lang.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class AseUser {

    @Id
    private String id;
    @Indexed(unique = true)
    @NonNull
    private String name;
    private String password;
    private String rfidToken; // TODO: make AseUser interface and create subclasses. Dispatchers don't have rFIDTokenString Should the rFIDTokenString be null for dispatchers? Depends on database. Maybe doesn't allow null.
    private String email;
    private UserRole role;
    private boolean isEnabled;

    public AseUser(String name, String password, String rfidToken, UserRole role) {
        this.name = name;
        this.password = password;
        this.rfidToken = rfidToken;
        this.role = role;
        this.isEnabled = true;
    }

    public AseUser(String id, String email, String name, String password, String rfidToken, UserRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.rfidToken = rfidToken;
        this.role = role;
        this.isEnabled = true;
    }

    // Checks if the delivery object contains valid information
    public boolean isValid() {
        try {
            if (Validation.isNullOrEmpty(this.name)
                    || Validation.isNullOrEmpty(this.password)
                    || Validation.isNullOrEmpty(this.rfidToken)
                    || Validation.isNullOrEmpty(this.email)
                    || Validation.isNullOrEmpty(this.role.toString())) {
                return false;
            }

            // TODO Perform additional checks
            return true;
        } catch (Exception e) {
            return false;
        }
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
