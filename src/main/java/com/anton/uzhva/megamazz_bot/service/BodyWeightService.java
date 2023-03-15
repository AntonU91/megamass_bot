package com.anton.uzhva.megamazz_bot.service;

import com.anton.uzhva.megamazz_bot.model.BodyWeight;
import com.anton.uzhva.megamazz_bot.repository.BodyWeightRepo;
import com.anton.uzhva.megamazz_bot.repository.ExerciseRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class BodyWeightService {
    BodyWeightRepo bodyWeightRepo;
    EntityManager entityManager;

    @Autowired
    public BodyWeightService(BodyWeightRepo bodyWeightRepo, EntityManager entityManager) {
        this.bodyWeightRepo = bodyWeightRepo;
        this.entityManager = entityManager;
    }

    @Transactional
    public void saveBodyWeight (BodyWeight bodyWeight) {
        bodyWeightRepo.save(bodyWeight);
    }

}
