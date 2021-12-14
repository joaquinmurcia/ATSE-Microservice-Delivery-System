package edu.tum.ase.asedelivery.asedeliverymodels;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@Document(collection = "boxes")
public class Box {
    @Id
    private String id;
    private Address address;
    private BoxStatus boxStatus;
    private String deliveryID;
}
