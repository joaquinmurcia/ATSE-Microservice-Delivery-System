package edu.tum.ase.asedelivery.deliverymanagement.repository;

import edu.tum.ase.asedelivery.asedeliverymodels.Delivery;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;

public interface DeliveryRepositoryCustom {
    //TODO Add paging support but probably not needed for the projects scope
    List<Delivery> findAll(Query query);
}
