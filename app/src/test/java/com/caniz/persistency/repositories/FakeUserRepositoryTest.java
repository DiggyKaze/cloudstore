package com.caniz.persistency.repositories;


import com.caniz.persistency.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class FakeUserRepositoryTest {

    private FakeUserRepository repo;

    @BeforeEach
    void setUp() {
        repo = new FakeUserRepository();
    }

    // ------------------------------------------------------------------
// save / findById
// ------------------------------------------------------------------
    @Test
    void save_assignsId_andFindByIdReturnsUser() {
        User alice = repo.save(new User("Alice", "alice@example.com"));

        assertThat(alice.getId()).isNotNull();
        assertThat(repo.findById(alice.getId()))
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getName()).isEqualTo("Alice");
                    assertThat(u.getEmail()).isEqualTo("alice@example.com");
                });
    }

    @Test
    void findById_unknownId_returnsEmpty() {
        assertThat(repo.findById(999L)).isEmpty();
    }

    // ------------------------------------------------------------------
// findByName
// ------------------------------------------------------------------
    @Test
    void findByName_returnsUser() {
        repo.save(new User("Bob", "bob@example.com"));

        Optional<User> result = repo.findByName("Bob");
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void findByName_unknownName_returnsEmpty() {
        assertThat(repo.findByName("Nobody")).isEmpty();
    }

    // ------------------------------------------------------------------
// findByEmail
// ------------------------------------------------------------------
    @Test
    void findByEmail_returnsUser() {
        repo.save(new User("Carol", "carol@example.com"));

        Optional<User> result = repo.findByEmail("carol@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Carol");
    }

    @Test
    void findByEmail_unknownEmail_returnsEmpty() {
        assertThat(repo.findByEmail("ghost@example.com")).isEmpty();
    }

    // ------------------------------------------------------------------
// update
// ------------------------------------------------------------------
    @Test
    void save_existingId_updatesUser() {
        User dave = repo.save(new User("Dave", "dave@example.com"));
        dave.setName("David");
        dave.setEmail("david@example.com");
        repo.save(dave);

        assertThat(repo.findById(dave.getId()))
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u.getName()).isEqualTo("David"));

// Old email index must be gone
        assertThat(repo.findByEmail("dave@example.com")).isEmpty();
        assertThat(repo.findByEmail("david@example.com")).isPresent();
    }

    // ------------------------------------------------------------------
// unique-email constraint
// ------------------------------------------------------------------
    @Test
    void save_duplicateEmail_throwsException() {
        repo.save(new User("Eve", "shared@example.com"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> repo.save(new User("Frank", "shared@example.com")))
                .withMessageContaining("shared@example.com");
    }

    // ------------------------------------------------------------------
// delete
// ------------------------------------------------------------------
    @Test
    void deleteById_removesUserAndIndexes() {
        User grace = repo.save(new User("Grace", "grace@example.com"));
        repo.deleteById(grace.getId());

        assertThat(repo.findById(grace.getId())).isEmpty();
        assertThat(repo.findByEmail("grace@example.com")).isEmpty();
        assertThat(repo.findByName("Grace")).isEmpty();
    }

    // ------------------------------------------------------------------
// count / existsById
// ------------------------------------------------------------------
    @Test
    void count_reflectsCurrentSize() {
        assertThat(repo.count()).isZero();
        repo.save(new User("Heidi", "heidi@example.com"));
        repo.save(new User("Ivan", "ivan@example.com"));
        assertThat(repo.count()).isEqualTo(2);
    }

    @Test
    void existsById_trueForSaved_falseForDeleted() {
        User judy = repo.save(new User("Judy", "judy@example.com"));
        assertThat(repo.existsById(judy.getId())).isTrue();
        repo.deleteById(judy.getId());
        assertThat(repo.existsById(judy.getId())).isFalse();
    }

    // ------------------------------------------------------------------
// defensive copy
// ------------------------------------------------------------------
    @Test
    void returnedUser_isDefensiveCopy_mutationDoesNotAffectStore() {
        User saved = repo.save(new User("Karl", "karl@example.com"));
        User fetched = repo.findById(saved.getId()).orElseThrow();
        fetched.setName("MUTATED");

        assertThat(repo.findById(saved.getId()))
                .hasValueSatisfying(u -> assertThat(u.getName()).isEqualTo("Karl"));
    }
}
