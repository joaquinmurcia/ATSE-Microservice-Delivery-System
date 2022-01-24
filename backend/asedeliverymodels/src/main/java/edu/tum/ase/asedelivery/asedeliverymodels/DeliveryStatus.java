package edu.tum.ase.asedelivery.asedeliverymodels;

import lombok.ToString;

@ToString
public enum DeliveryStatus {
    open,
    collected,
    pickedUp,
    delivered
}