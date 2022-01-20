package edu.tum.ase.asedelivery.usermngmt.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class BeanConfig {
    public @Bean
    MongoClient mongoClient() {
        return MongoClients.create("mongodb://" + System.getenv().getOrDefault("MONGO_DB_HOSTNAME", "localhost") + ":27017");
    }
    public @Bean
    MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), System.getenv().getOrDefault("MONGO_DB_DATABASE", "userManagement"));
    }
}
