package edu.tum.ase.asedelivery.boxmanagement.repository;

import edu.tum.ase.asedelivery.boxmanagement.model.Box;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;

public interface BoxRepositoryCustom {
    //TODO Add paging support but probably not needed for the projects scope
    List<Box> findAll(Query query);
}
