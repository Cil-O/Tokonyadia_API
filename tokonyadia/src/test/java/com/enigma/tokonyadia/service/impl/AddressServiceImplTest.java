package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Address;
import com.enigma.tokonyadia.repository.AddressRepository;
import com.enigma.tokonyadia.service.AddressService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    private AddressService addressService;
    @BeforeEach
    void setUp() {
        addressService = new AddressServiceImpl(addressRepository);
    }
    @Test
    void create() {
        // Given
        Address address = Address.builder()
                .id("1")
                .street("Jl. Sudirman")
                .city("Jakarta")
                .province("Jakut")
                .build();
        when(addressRepository.save(address)).thenReturn(address);

        // When
        Address actualAddress = addressService.create(address);

        // Then
        Assertions.assertNotNull(actualAddress);
        Assertions.assertEquals(address.getId(), actualAddress.getId());

        // Verify that the save method is called once with the correct parameter
        verify(addressRepository, times(1)).save(address);
    }
}