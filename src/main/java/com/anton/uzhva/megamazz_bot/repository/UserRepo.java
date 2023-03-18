package com.anton.uzhva.megamazz_bot.repository;

import com.anton.uzhva.megamazz_bot.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo  extends CrudRepository<User, Long> {
}
