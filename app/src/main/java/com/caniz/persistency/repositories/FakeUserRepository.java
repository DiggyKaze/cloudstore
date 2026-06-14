package com.caniz.persistency.repositories;


import com.caniz.persistency.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Thread-safe, in-memory fake implementation of {@link UserRepository}.
 *
 * <p>Design notes:
 * <ul>
 *   <li>{@code store}      — primary index, keyed by generated ID.</li>
 *   <li>{@code emailIndex} — secondary unique index for O(1) email look-ups.</li>
 *   <li>{@code nameIndex}  — secondary index for O(1) name look-ups.</li>
 *   <li>All write operations are {@code synchronized} on {@code this} so that
 *       the primary store and both indexes are always mutated together atomically,
 *       while reads on the individual ConcurrentHashMaps remain lock-free.</li>
 *   <li>Every returned entity is a defensive copy so callers cannot accidentally
 *       corrupt stored state.</li>
 * </ul>
 */
@Repository
public class FakeUserRepository implements UserRepository {

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    /** Primary store: id → User */
    private final ConcurrentHashMap<Long, User> store = new ConcurrentHashMap<>();

    /** Unique index: email → id */
    private final ConcurrentHashMap<String, Long> emailIndex = new ConcurrentHashMap<>();

    /** Index: name → id  (first match wins; names are not guaranteed unique) */
    private final ConcurrentHashMap<String, Long> nameIndex = new ConcurrentHashMap<>();

    /** Auto-increment surrogate key, mirrors GenerationType.IDENTITY */
    private final AtomicLong idSequence = new AtomicLong(1);

    // -------------------------------------------------------------------------
    // Reads  (lock-free — ConcurrentHashMap guarantees visibility)
    // -------------------------------------------------------------------------

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id)).map(this::copy);
    }

    @Override
    public Optional<User> findByName(String name) {
        Long id = nameIndex.get(name);
        return id == null ? Optional.empty() : findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Long id = emailIndex.get(email);
        return id == null ? Optional.empty() : findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public List<User> findAll() {
        return store.values().stream()
                .map(this::copy)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAllById(Iterable<Long> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
                .map(store::get)
                .filter(Objects::nonNull)
                .map(this::copy)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return store.size();
    }

    // -------------------------------------------------------------------------
    // Writes  (synchronized to keep store + indexes consistent)
    // -------------------------------------------------------------------------

    @Override
    public synchronized <S extends User> S save(S user) {
        if (user.getId() == null) {
            // INSERT: assign a new ID
            user.setId(idSequence.getAndIncrement());
        } else {
            // UPDATE: remove stale index entries for the previous version
            User previous = store.get(user.getId());
            if (previous != null) {
                emailIndex.remove(previous.getEmail());
                nameIndex.remove(previous.getName());
            }
        }

        validateUniqueEmail(user);

        store.put(user.getId(), copy(user));
        emailIndex.put(user.getEmail(), user.getId());
        nameIndex.put(user.getName(), user.getId());

        return user;
    }

    @Override
    public synchronized <S extends User> List<S> saveAll(Iterable<S> users) {
        List<S> saved = new ArrayList<>();
        users.forEach(u -> saved.add(save(u)));
        return saved;
    }

    @Override
    public synchronized void deleteById(Long id) {
        User removed = store.remove(id);
        if (removed != null) {
            emailIndex.remove(removed.getEmail());
            nameIndex.remove(removed.getName());
        }
    }

    @Override
    public synchronized void delete(User user) {
        deleteById(user.getId());
    }

    @Override
    public synchronized void deleteAll(Iterable<? extends User> users) {
        users.forEach(this::delete);
    }

    @Override
    public synchronized void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public synchronized void deleteAll() {
        store.clear();
        emailIndex.clear();
        nameIndex.clear();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Shallow defensive copy — keeps callers isolated from the stored instance. */
    private User copy(User source) {
        User copy = new User(source.getName(), source.getEmail());
        copy.setId(source.getId());
        return copy;
    }

    /**
     * Enforces unique-email constraint, mirroring the database behaviour
     * that a real JPA implementation would provide via a unique index.
     */
    private void validateUniqueEmail(User candidate) {
        Long existingId = emailIndex.get(candidate.getEmail());
        if (existingId != null && !existingId.equals(candidate.getId())) {
            throw new IllegalArgumentException(
                    "A user with email '" + candidate.getEmail() + "' already exists (id=" + existingId + ")");
        }
    }
}