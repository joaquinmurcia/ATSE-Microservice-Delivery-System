package edu.tum.ase.asedelivery.usermngmt.model;

import lombok.ToString;

@ToString
public enum DeliveryStatus {
    open,
    collected,
    pickedUp,
    delivered
}