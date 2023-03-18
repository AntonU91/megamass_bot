package com.anton.uzhva.megamazz_bot.service;

import com.anton.uzhva.megamazz_bot.model.BodyWeight;
import com.anton.uzhva.megamazz_bot.repository.BodyWeightRepo;
import com.anton.uzhva.megamazz_bot.util.Period;
import lombok.AccessLevel;
import lombok.ToString;
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
@ToString
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

    public Object getTheLatestRecord(long chatId) {
        return entityManager.createQuery("FROM bodyWeight bw where bw.user.id=:id order by bw.created_at DESC")
                .setParameter("id", chatId)
                .setMaxResults(1)
                .getSingleResult();
    }

    public boolean matchSpecifiedDiapason(long chatId, int daysToSubtract) {
        if (daysToSubtract == 0) return false;
        BodyWeight theLatestRecord = (BodyWeight) getTheLatestRecord(chatId);
        Date theLatestRecordDate = theLatestRecord.created_at();
        String firstQuery = "SELECT IF(COUNT(*)>0,1,0) FROM body_weight WHERE created_at BETWEEN :startDate - INTERVAL :daysToSubtract DAY AND :startDate";
        String secondQuery = "SELECT IF(COUNT(*)>0,1,0) FROM body_weight WHERE created_at <:startDate - INTERVAL :daysToSubtract DAY AND :startDate";
        BigInteger firstQueryResult = (BigInteger) getQueryResult(daysToSubtract, theLatestRecordDate, firstQuery, "startDate", "daysToSubtract");
        BigInteger secondQueryResult = (BigInteger) getQueryResult(daysToSubtract, theLatestRecordDate, secondQuery, "startDate", "daysToSubtract");
        int resultValue = firstQueryResult.add(secondQueryResult).intValue();
        return resultValue > 1;
    }


    private Object getQueryResult(int daysToSubtract, Date theLatestRecordDate, String query, String... parameters) {
        return entityManager.createNativeQuery(query)
                .setParameter(parameters[0], theLatestRecordDate)
                .setParameter(parameters[1], daysToSubtract)
                .getSingleResult();
    }

    public List<BodyWeight> getResultsOfSpecifiedDiapason(long chatId, Period period) {
        int daysToSubtract = period.getDays();
        BodyWeight theLatestRecord = (BodyWeight) getTheLatestRecord(chatId);
        Date theLatestRecordDate = theLatestRecord.created_at();
        String sqlQuery;
        if (daysToSubtract == 0 && period.getName().equals("Last result")) {
            sqlQuery = "SELECT MAX(creared_at) FROM body_weight";
            return entityManager.createNativeQuery(sqlQuery)
                    .getResultList();
        } else if (daysToSubtract == 0 && period.getName().equals("All results")) {
            sqlQuery = "FROM bodyWeight bw WHERE bw.user.id=:id";
            return entityManager.createQuery(sqlQuery)
                    .setParameter("id",chatId)// TODO CHECK
                    .getResultList();
        } else {
            sqlQuery = "SELECT * FROM body_weight WHERE created_at BETWEEN :startDate - INTERVAL :daysToSubtract DAY AND :startDate";
            return entityManager.createNativeQuery(sqlQuery)
                    .setParameter("startDate", theLatestRecordDate)
                    .setParameter("daysToSubtract", daysToSubtract)
                    .getResultList();
        }
    }

    public boolean hasAtLeastOneRecord(long chatId) {
        String sqlQuery = "SELECT * FROM body_weight  WHERE  created_at >0 LIMIT 1";
        List<BodyWeight> bodyWeightList = entityManager.createNativeQuery(sqlQuery)
                .getResultList();
        return !bodyWeightList.isEmpty();
    }

}
