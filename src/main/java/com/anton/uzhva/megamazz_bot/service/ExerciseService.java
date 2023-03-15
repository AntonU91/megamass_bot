package com.anton.uzhva.megamazz_bot.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.repository.ExerciseRepo;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@org.springframework.stereotype.Service
@Transactional(readOnly = true)
public class ExerciseService {
    ExerciseRepo exerciseRepo;
    EntityManager eManager;

    @Autowired
    public ExerciseService(ExerciseRepo exerciseRepo, EntityManager eManager) {
        this.eManager = eManager;
        this.exerciseRepo = exerciseRepo;
    }

    public Optional<Exercise> findExcerciseById(long chatId) {
        return exerciseRepo.findById(chatId);
    }

    @Transactional
    public void saveExercise(Exercise exercise) {
        exerciseRepo.save(exercise);
    }


    public List<Exercise> findAllExerciseRecordByUserId(long chatId) {
        return eManager.createQuery(" FROM exercise e where e.user.id=:id")
                .setParameter("id", chatId).getResultList();
    }

    public List<Exercise> findAtLeastOneExerciceRecordByUserId(long chatId) {
        return eManager.createQuery("FROM exercise e where e.user.id=:id").setParameter("id", chatId)
                .setMaxResults(1)
                .getResultList();
    }

    public Object getTheErliestRecord(long chatId) {
        return eManager.createQuery("FROM exercise e where e.user.id=:id order by e.recordDate")
                .setParameter("id", chatId)
                .setMaxResults(1).getSingleResult();
    }

    public List<Integer> getListOfTrainingWeeksNumber(long chatId) {
        return eManager
                .createQuery("SELECT DISTINCT e.weekNumber FROM exercise e WHERE e.user.id=:id ORDER BY e.weekNumber")
                .setParameter("id", chatId)
                .getResultList();
    }

    public List<Exercise> getTrainingResultOfConcreteWeek(long chatId, int weekNumber) {
        return eManager.createQuery("  FROM exercise e WHERE  e.user.id=:id AND e.weekNumber=:weekNumber ORDER BY e.name, e.weight")
                .setParameter("weekNumber", weekNumber)
                .setParameter("id", chatId).getResultList();
    }

    public Object findExerciseByRecordDate(Date date) {
        return eManager.createQuery("FROM exercise e WHERE e.recordDate=:date").setParameter("date", date)
                .getSingleResult();
    }

    public List<Exercise> getAllTrainingsResults(long chatId) {
        return eManager.createQuery("FROM exercise e WHERE  e.user.id=:id ORDER BY e.weekNumber, e.name, e.weight")
                .setParameter("id", chatId).getResultList();
    }

    @Transactional
    public void deleteAllUserTrainingsResults(long chatId) {
        eManager.createQuery("DELETE FROM exercise e WHERE e.user.id=:chatId")
                .setParameter("chatId", chatId).executeUpdate();
    }

    public Object getExercisesCountByUserID(long chatId) {
        return eManager.createQuery(" SELECT COUNT(*) FROM exercise e WHERE e.user.id=:id ")
                .setParameter("id", chatId).getSingleResult();
    }

}
