package edu.tum.ase.asedelivery.usermngmt.repository;

import edu.tum.ase.asedelivery.usermngmt.model.AseUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<AseUserDAO> findAll(Query query) {
        return mongoTemplate.find(query, AseUserDAO.class);
    }
}