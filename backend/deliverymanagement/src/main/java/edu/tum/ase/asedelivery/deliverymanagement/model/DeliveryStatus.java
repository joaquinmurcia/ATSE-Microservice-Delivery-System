package edu.tum.ase.asedelivery.deliverymanagement.model;

import lombok.ToString;

@ToString
public enum DeliveryStatus {
    open,
    collected,
    pickedUp,
    delivered
}