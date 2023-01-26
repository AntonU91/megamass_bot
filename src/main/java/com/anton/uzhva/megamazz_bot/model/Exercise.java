package com.anton.uzhva.megamazz_bot.model;

import com.anton.uzhva.megamazz_bot.util.ExerciseName;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity(name = "exercise")
public class Exercise {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "exercise_name")
    @Enumerated(EnumType.STRING)
    @NotEmpty
    private ExerciseName name;

    @Column(name = "weight")
    @NotBlank
    private double weight;

    @Column(name="count")
    @NotBlank
    private int count;

    @Column(name = "record_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordDate;

    @Column(name="week_number")
    private int weekNumber;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "chat_id")
     private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ExerciseName getName() {
        return name;
    }

    public void setName(ExerciseName name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    
    
}
