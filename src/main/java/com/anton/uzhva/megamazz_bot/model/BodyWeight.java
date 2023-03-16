package com.anton.uzhva.megamazz_bot.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Component
@Accessors(fluent = true)
@Entity(name = "bodyWeight")
@Table(name = "body_weight")
@ToString
public class BodyWeight {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "chat_id")
    private User user;

    @NonNull
    @Column(name = "value", nullable = false)
    private double value;

    @NonNull
    @Column(name = "created_at")
    private Date created_at;

}
