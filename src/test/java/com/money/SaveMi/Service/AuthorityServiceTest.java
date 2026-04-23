package com.money.SaveMi.Service;

import com.money.SaveMi.Model.Authority;
import com.money.SaveMi.Repo.AuthorityRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorityServiceTest {

    @Mock
    private AuthorityRepo authorityRepo;

    @InjectMocks
    private AuthorityService authorityService;

    private Authority adminAuthority;
    private Authority userAuthority;

    @BeforeEach
    void setUp() {
        adminAuthority = new Authority(1L, Authority.Role.ADMIN);
        userAuthority = new Authority(2L, Authority.Role.USER);
    }

    @Test
    void testSaveAuthority() {
        authorityService.saveAuthority(adminAuthority);
        verify(authorityRepo, times(1)).save(adminAuthority);
    }

    @Test
    void testAuthorityExistsTrue() {
        when(authorityRepo.findAll()).thenReturn(Arrays.asList(adminAuthority, userAuthority));
        
        boolean exists = authorityService.authorityExists(Authority.Role.ADMIN);
        
        assertTrue(exists);
        verify(authorityRepo, times(1)).findAll();
    }

    @Test
    void testAuthorityExistsFalse() {
        when(authorityRepo.findAll()).thenReturn(Collections.singletonList(userAuthority));
        
        boolean exists = authorityService.authorityExists(Authority.Role.ADMIN);
        
        assertFalse(exists);
        verify(authorityRepo, times(1)).findAll();
    }

    @Test
    void testAuthorityExistsEmptyList() {
        when(authorityRepo.findAll()).thenReturn(Collections.emptyList());
        
        boolean exists = authorityService.authorityExists(Authority.Role.ADMIN);
        
        assertFalse(exists);
        verify(authorityRepo, times(1)).findAll();
    }
}
