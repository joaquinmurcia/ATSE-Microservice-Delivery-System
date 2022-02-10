package edu.tum.ase.asedelivery.boxmanagement.model;

import edu.tum.ase.asedelivery.boxmanagement.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public BoxStatus getBoxStatus() {
        return boxStatus;
    }

    public void setBoxStatus(BoxStatus boxStatus) {
        this.boxStatus = boxStatus;
    }

    public List<String> getDeliveryIDs() {
        return deliveryIDs;
    }

    public void setDeliveryIDs(List<String> deliveryIDs) {
        this.deliveryIDs = deliveryIDs;
    }

    public String getRaspberryPiID() {
        return raspberryPiID;
    }

    public void setRaspberryPiID(String raspberryPiID) {
        this.raspberryPiID = raspberryPiID;
    }
}
