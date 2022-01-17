package edu.tum.ase.asedelivery.boxmanagement.service;

import edu.tum.ase.asedelivery.asedeliverymodels.Delivery;
import edu.tum.ase.asedelivery.boxmanagement.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    // Get data
    public List<Delivery> findAll(Query query) {
        return deliveryRepository.findAll(query);
    }

    public Optional<Delivery> findById(String id) {
        return deliveryRepository.findById(id);
    }

    // Store data
    public Delivery save(Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

    public List<Delivery> saveAll(List<Delivery> deliveries) {
        return deliveryRepository.saveAll(deliveries);
    }

    // Remove Data
    public void deleteById(String id) {
        deliveryRepository.deleteById(id);
    }
}
