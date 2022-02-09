package edu.tum.ase.asedelivery.boxmanagement.model;

import lombok.ToString;

@ToString
public enum DeliveryStatus {
    open,
    collected,
    pickedUp,
    delivered
}