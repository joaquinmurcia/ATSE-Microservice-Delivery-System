package edu.tum.ase.asedelivery.usermngmt.model;

public class Validation {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmpty(DeliveryStatus value) {
        return value == null || value.toString().isEmpty();
    }

}
