package edu.tum.ase.asedelivery.usermngmt.model;

import com.mongodb.lang.NonNull;
import edu.tum.ase.asedelivery.usermngmt.model.Validation;
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
    private String rfidToken;
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

}
