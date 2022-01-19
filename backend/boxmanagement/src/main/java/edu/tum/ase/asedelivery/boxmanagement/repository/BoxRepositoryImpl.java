package edu.tum.ase.asedelivery.boxmanagement.repository;

import edu.tum.ase.asedelivery.asedeliverymodels.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class BoxRepositoryImpl implements BoxRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public BoxRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Box> findAll(Query query) {
        return mongoTemplate.find(query, Box.class);
    }
}
