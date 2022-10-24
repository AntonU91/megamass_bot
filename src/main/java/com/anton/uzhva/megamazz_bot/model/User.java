package com.anton.uzhva.megamazz_bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "user")
public class User {
    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name="first_name")
    private String firstName;

    @Column(name="user_login")
    private String userLogin;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userName) {
        this.userLogin = userName;
    }

    public Long getId() {
        return chatId;
    }

    public void setId(Long id) {
        this.chatId =id;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", userName='" + userLogin + '\'' +
                '}';
    }
}
