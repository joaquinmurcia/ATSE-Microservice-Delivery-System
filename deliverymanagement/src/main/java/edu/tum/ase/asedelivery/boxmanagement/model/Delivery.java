package edu.tum.ase.asedelivery.boxmanagement.model;

import edu.tum.ase.asedelivery.boxmanagement.utils.Validation;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@Document(collection = "deliveries")
public class Delivery {

    @Id
    private String id;
    private String targetBox;
    private String targetCustomer;
    private String responsibleDriver;
    private DeliveryStatus deliveryStatus;

    // Checks if the delivery object contains valid information
    public boolean isValid() {
        try {
            if (Validation.isNullOrEmpty(this.targetBox)
                    || Validation.isNullOrEmpty(this.targetCustomer)
                    || Validation.isNullOrEmpty(this.responsibleDriver)
                    || Validation.isNullOrEmpty(this.deliveryStatus.toString())) {
                return false;
            }

            // TODO Perform additional checks
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
