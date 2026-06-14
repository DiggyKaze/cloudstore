package com.caniz.persistency.repositories;


import com.caniz.persistency.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Mirrors the Spring Data JPA repository interface.
 * The fake implementation (FakeUserRepository) uses in-memory ConcurrentHashMaps,
 * making it ideal for unit tests — no database or Spring context required.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    // findById(Long id) is already declared by CrudRepository<User, Long>
    @Override
    List<User> findAll();
}