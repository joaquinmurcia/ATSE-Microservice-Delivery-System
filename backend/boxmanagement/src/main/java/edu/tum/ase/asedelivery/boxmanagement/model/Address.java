package edu.tum.ase.asedelivery.boxmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String streetName;
    private int streetNumber;
    private int postcode;
    private String city;
    private String country;
}
