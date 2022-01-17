package edu.tum.ase.asedelivery.asedeliverymodels;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "deliveries")
public class Delivery {

    @Id
    private String id;
    private String targetBox;
    private String targetCustomer;
    private String targetCustomerRFIDToken;
    private String responsibleDeliverer;
    private String responsibleDelivererRfidToken;
    private DeliveryStatus deliveryStatus;

    // Checks if the delivery object contains valid information
    public boolean isValid() {
        try {
            if (Validation.isNullOrEmpty(this.targetBox)
                    || Validation.isNullOrEmpty(this.targetCustomer)
                    || Validation.isNullOrEmpty(this.responsibleDeliverer)
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
