package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Admin;
import com.enigma.tokonyadia.repository.AddressRepository;
import com.enigma.tokonyadia.repository.AdminRepository;
import com.enigma.tokonyadia.service.AddressService;
import com.enigma.tokonyadia.service.AdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {


    @Mock
    private AdminRepository adminRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(adminRepository);
    }

    @Test
    void shouldReturnAdminWhenCreateNewAdmin() {
        // Given
        Admin admin = Admin.builder()
                .id("1")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        when(adminRepository.save(admin)).thenReturn(admin);

        // When
        Admin actualAdmin = adminService.create(admin);

        // Then
        Assertions.assertNotNull(actualAdmin);
        Assertions.assertEquals(admin.getId(), actualAdmin.getId());

        // Verify
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void createShouldThrowConflictException() {
        // Given
        Admin admin = Admin.builder()
                .id("1")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        when(adminRepository.save(admin)).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        // When/Then
        Assertions.assertThrows(ResponseStatusException.class, () -> {
            adminService.create(admin);
        });

        // Verify that the save method is called once with the correct parameter
        verify(adminRepository, times(1)).save(admin);
    }
}