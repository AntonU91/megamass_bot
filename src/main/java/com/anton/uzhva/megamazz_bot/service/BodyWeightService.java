package com.anton.uzhva.megamazz_bot.service;

import com.anton.uzhva.megamazz_bot.model.BodyWeight;
import com.anton.uzhva.megamazz_bot.repository.BodyWeightRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

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
    public void saveBodyWeight(BodyWeight bodyWeight) {
        bodyWeightRepo.save(bodyWeight);
    }


    private Object getTheEarliestRecord(long chatId) {
        return entityManager.createQuery("FROM bodyWeight bw where bw.user.id=:id order by bw.created_at")
                .setParameter("id", chatId)
                .getSingleResult();
    }

    private Object getTheLatestRecord(long chatId) {
        return entityManager.createQuery("FROM bodyWeight bw where bw.user.id=:id order by bw.created_at DESC")
                .setParameter("id", chatId)
                .setMaxResults(1)
                .getSingleResult();
    }

    public boolean hasAtLeastOneRecordInSpecifiedDiapason(long chatId, int daysToSubtract) {
        BodyWeight theLatestRecord = (BodyWeight) getTheLatestRecord(chatId);
        Date theLatestRecordDate = theLatestRecord.created_at();
        String sqlQuery = "SELECT COUNT(*) FROM body_weight WHERE created_at  " +
                "BETWEEN :startDate - INTERVAL :daysToSubtract DAY AND :startDate";
        BigInteger count = (BigInteger) entityManager.createNativeQuery(sqlQuery)
                .setParameter("startDate", theLatestRecordDate)
                .setParameter("daysToSubtract", daysToSubtract)
                .getSingleResult();
        return count.intValue() > 0;
    }

    public List<BodyWeight> getBodyWeightResultsInSpecifiedDiapason(long chatId, int daysToSubtract) {
        BodyWeight theLatestRecord = (BodyWeight) getTheLatestRecord(chatId);
        Date theLatestRecordDate = theLatestRecord.created_at();
        String sqlQuery = "SELECT * FROM body_weight WHERE created_at BETWEEN :startDate - INTERVAL :daysToSubtract DAY AND :startDate ";
        return entityManager.createNativeQuery(sqlQuery, BodyWeight.class)
                .setParameter("startDate", theLatestRecordDate)
                .setParameter("daysToSubtract", daysToSubtract)
                .getResultList();

    }

}
