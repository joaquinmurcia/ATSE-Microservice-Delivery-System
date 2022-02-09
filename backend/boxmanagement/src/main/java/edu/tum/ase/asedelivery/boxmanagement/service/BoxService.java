package edu.tum.ase.asedelivery.boxmanagement.service;

import edu.tum.ase.asedelivery.boxmanagement.model.Box;
import edu.tum.ase.asedelivery.boxmanagement.repository.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BoxService {

    @Autowired
    private BoxRepository boxRepository;

    // Get data
    public List<Box> findAll(Query query) {
        return boxRepository.findAll(query);
    }

    public Optional<Box> findById(String id) {
        return boxRepository.findById(id);
    }

    // Store data
    public Box save(Box delivery) {
        return boxRepository.save(delivery);
    }

    public List<Box> saveAll(List<Box> deliveries) {
        return boxRepository.saveAll(deliveries);
    }

    // Remove Data
    public void deleteById(String id) {
        boxRepository.deleteById(id);
    }
}
