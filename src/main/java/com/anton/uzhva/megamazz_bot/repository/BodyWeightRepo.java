package com.anton.uzhva.megamazz_bot.repository;

import com.anton.uzhva.megamazz_bot.model.BodyWeight;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BodyWeightRepo extends CrudRepository<BodyWeight, Long> {

}
