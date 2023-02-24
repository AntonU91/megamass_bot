package com.anton.uzhva.megamazz_bot.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepo extends CrudRepository<Exercise, Long> {

}
