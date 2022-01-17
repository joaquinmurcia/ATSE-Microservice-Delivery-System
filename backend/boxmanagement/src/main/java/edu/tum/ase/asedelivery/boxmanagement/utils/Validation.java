package edu.tum.ase.asedelivery.boxmanagement.utils;

import edu.tum.ase.asedelivery.asedeliverymodels.BoxStatus;

public class Validation {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmpty(BoxStatus value) {
        return value == null || value.toString().isEmpty();
    }

}
