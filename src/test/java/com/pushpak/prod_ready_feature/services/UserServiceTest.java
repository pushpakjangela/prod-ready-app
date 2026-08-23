package com.pushpak.prod_ready_feature.services;

import com.pushpak.prod_ready_feature.TestContainerConfiguration;
import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.enums.Permission;
import com.pushpak.prod_ready_feature.enums.Role;
import com.pushpak.prod_ready_feature.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestContainerConfiguration.class)
@DataJpaTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("JANGELA")
                .email("jangela@gmail.com")
                .password("JANGELA@123")
                .roles(Set.of(Role.USER))
                .permissions(Set.of(Permission.POST_VIEW, Permission.POST_CREATE))
                .build();
    }

    @Test
    void testLoadUserByUsername_whenUsernameIsValid_returnUser() {
        when(userRepository.getByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(user.getEmail());
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getUsername());

        verify(userRepository, times(1)).getByEmail(user.getEmail());
    }

    @Test
    void testLoadUserByUsername_whenUsernameIsInvalid_returnNull() {
        String invalidEmail = "nonexistent@gmail.com";
        when(userRepository.getByEmail(invalidEmail)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> {
            userService.loadUserByUsername(invalidEmail);
        });

        verify(userRepository, times(1)).getByEmail(invalidEmail);
    }

    @Test
    void testGetUserById_whenIdIsValid_returnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getUserById(user.getId());
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testGetUserById_whenIdIsInvalid_returnNull() {
        Long invalidId = 999L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserById(invalidId);
        });

        verify(userRepository, times(1)).findById(invalidId);
    }
}