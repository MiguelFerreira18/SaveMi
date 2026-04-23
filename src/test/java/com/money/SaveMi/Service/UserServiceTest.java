package com.money.SaveMi.Service;

import com.money.SaveMi.Model.Authority;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.AuthorityRepo;
import com.money.SaveMi.Repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthorityRepo authorityRepo;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Authority userAuthority;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        
        userAuthority = new Authority(Authority.Role.USER);
    }

    @Test
    void testSaveUserSuccess() {
        when(authorityRepo.findByAuthority(Authority.Role.USER)).thenReturn(Optional.of(userAuthority));
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Optional<User> result = userService.saveUser(testUser);

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        assertTrue(result.get().getAuthorities().contains(userAuthority));
    }

    @Test
    void testSaveUserAuthorityNotFound() {
        when(authorityRepo.findByAuthority(Authority.Role.USER)).thenReturn(Optional.empty());

        Optional<User> result = userService.saveUser(testUser);

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveUserException() {
        when(authorityRepo.findByAuthority(Authority.Role.USER)).thenReturn(Optional.of(userAuthority));
        when(userRepo.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        Optional<User> result = userService.saveUser(testUser);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByEmail() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        User result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails result = userService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown@example.com"));
    }
}
