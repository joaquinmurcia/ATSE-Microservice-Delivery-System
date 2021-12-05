package edu.tum.ase.asedelivery.deliverymanagement.repository;

import edu.tum.ase.asedelivery.deliverymanagement.model.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom{
    private final MongoTemplate mongoTemplate;

    @Autowired
    public DeliveryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Delivery> findAll(Query query) {
        return mongoTemplate.find(query, Delivery.class);
    }
}
