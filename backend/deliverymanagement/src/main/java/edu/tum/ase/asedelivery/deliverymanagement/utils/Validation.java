package edu.tum.ase.asedelivery.deliverymanagement.utils;

import edu.tum.ase.asedelivery.asedeliverymodels.DeliveryStatus;

public class Validation {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmpty(DeliveryStatus value) {
        return value == null || value.toString().isEmpty();
    }

}
