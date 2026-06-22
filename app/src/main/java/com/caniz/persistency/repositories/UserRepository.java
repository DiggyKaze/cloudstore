package com.caniz.persistency.repositories;


import com.caniz.persistency.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);


    @Override
    List<User> findAll();
}