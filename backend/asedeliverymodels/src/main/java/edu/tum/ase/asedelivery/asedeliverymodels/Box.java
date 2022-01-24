package edu.tum.ase.asedelivery.asedeliverymodels;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "boxes")
public class Box {
    @Id
    private String id;
    private Address address;
    private BoxStatus boxStatus;
    private List<String> deliveryIDs;
    private String raspberryPiID;

    public Box copyWith(Box box){
        if (box.id == null) {box.id = this.id;}
        if (box.address == null) {box.address = this.address;}
        if (box.boxStatus == null) {box.boxStatus = this.boxStatus;}
        if (box.deliveryIDs == null) {box.deliveryIDs = this.deliveryIDs;}
        if (box.raspberryPiID == null) {box.raspberryPiID = this.raspberryPiID;}

        return box;
    }
}
