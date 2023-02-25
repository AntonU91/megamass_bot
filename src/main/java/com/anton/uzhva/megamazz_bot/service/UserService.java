package com.anton.uzhva.megamazz_bot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.anton.uzhva.megamazz_bot.model.User;
import com.anton.uzhva.megamazz_bot.model.UserRepo;

@org.springframework.stereotype.Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepo userRepo;
    private final EntityManager eManager;

    @Autowired
    public UserService(UserRepo userRepo, EntityManager eManager) {
        this.eManager = eManager;
        this.userRepo = userRepo;
    }


    public String getUserLogin(long chatId) {
        return (String) eManager.createQuery("SELECT u.userLogin FROM user u WHERE u.chatId=:chatId")
                .setParameter("chatId", chatId)
                .getSingleResult();
    }

    @Transactional
    public void addExercise(long chatId, String newExercise) {
        User user = userRepo.findById(chatId).get();

        String exercises = (String) eManager.createQuery("SELECT u.exercises FROM user u WHERE u.chatId=:id")
                .setParameter("id", chatId)
                .getSingleResult();
        exercises += " ," + newExercise;
        user.addExercise(newExercise);
        userRepo.save(user);
    }

    public List<String> getExerciseList(long chatId) {
        List<String> exerciseList = new ArrayList<>();
        String exercises = (String) eManager.createQuery("SELECT u.exercises FROM user u WHERE u.chatId=:id")
                .setParameter("id", chatId)
                .getSingleResult();
        System.out.println(exercises);
        String[] strArr = exercises.split(",");
        for (String temp : strArr) {
            exerciseList.add(temp);
        }
        return exerciseList;
    }

}
