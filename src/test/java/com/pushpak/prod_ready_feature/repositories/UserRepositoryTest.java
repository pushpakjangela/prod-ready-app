package com.pushpak.prod_ready_feature.repositories;

import com.pushpak.prod_ready_feature.TestContainerConfiguration;
import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.enums.Permission;
import com.pushpak.prod_ready_feature.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(TestContainerConfiguration.class)
class   UserRepositoryTest {

    @Container
    @ServiceConnection
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest");

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("JANGELA")
                .email("jangela@gmail.com")
                .password("JANGELA@123")
                .roles(Set.of(Role.USER))
                .permissions(Set.of(Permission.POST_VIEW, Permission.POST_CREATE))
                .build();
        user = userRepository.save(user);
    }

    @Test
    void testGetByEmail_whenEmailIsValid_returnsUser() {
        Optional<User> foundUser = userRepository.getByEmail(user.getEmail());
        assertTrue(foundUser.isPresent());
        User retrievedUser = foundUser.get();
        assertEquals(user.getEmail(), retrievedUser.getEmail());
        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getPassword(), retrievedUser.getPassword());
        assertEquals(user.getRoles(), retrievedUser.getRoles());
        assertEquals(user.getPermissions(), retrievedUser.getPermissions());
    }

    @Test
    void testGetByEmail_whenEmailIsInvalid_returnsNull() {
        Optional<User> foundUser = userRepository.getByEmail("nonexistent@gmail.com");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void testGetUserById_whenIdIsValid_returnsUser() {
        User foundUser = userRepository.getUserById(user.getId());
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getPassword(), foundUser.getPassword());
        assertEquals(user.getRoles(), foundUser.getRoles());
        assertEquals(user.getPermissions(), foundUser.getPermissions());
    }

    @Test
    void testGetUserById_whenIdIsInvalid_returnsNull() {
        User foundUser = userRepository.getUserById(999L);
        assertNull(foundUser);
    }

    @Test
    void testGetAllUsers_returnsAllUsers() {
        java.util.List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail())));
    }
}