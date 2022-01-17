package edu.tum.ase.asedelivery.usermngmt.repository;

import edu.tum.ase.asedelivery.usermngmt.model.AseUserDAO;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface UserRepositoryCustom {
    //TODO Add paging support but probably not needed for the projects scope
    List<AseUserDAO> findAll(Query query);
}