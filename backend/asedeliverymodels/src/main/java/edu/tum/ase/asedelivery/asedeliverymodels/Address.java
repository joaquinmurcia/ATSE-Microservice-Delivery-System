package edu.tum.ase.asedelivery.asedeliverymodels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Address {
    private String streetName;
    private int streetNumber;
    private int postcode;
    private String city;
    private String country;
}
