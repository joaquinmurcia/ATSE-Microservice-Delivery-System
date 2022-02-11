package edu.tum.ase.asedelivery.boxmanagement.model;

import edu.tum.ase.asedelivery.boxmanagement.model.Validation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public Delivery copyWith(Delivery delivery){
        if (delivery.id == null) {delivery.id = this.id;}
        if (delivery.targetBox == null) {delivery.targetBox = this.targetBox;}
        if (delivery.targetCustomer == null) {delivery.targetCustomer = this.targetCustomer;}
        if (delivery.targetCustomerRFIDToken == null) {delivery.targetCustomerRFIDToken = this.targetCustomerRFIDToken;}
        if (delivery.responsibleDeliverer == null) {delivery.responsibleDeliverer = this.responsibleDeliverer;}
        if (delivery.responsibleDelivererRfidToken == null) {delivery.responsibleDelivererRfidToken = this.responsibleDelivererRfidToken;}
        if (delivery.deliveryStatus == null) {delivery.deliveryStatus = this.deliveryStatus;}

        return delivery;
    }
}
