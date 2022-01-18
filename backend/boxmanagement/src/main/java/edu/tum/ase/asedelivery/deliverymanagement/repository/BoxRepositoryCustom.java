package edu.tum.ase.asedelivery.deliverymanagement.repository;

import edu.tum.ase.asedelivery.asedeliverymodels.Box;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;

public interface BoxRepositoryCustom {
    //TODO Add paging support but probably not needed for the projects scope
    List<Box> findAll(Query query);
}
