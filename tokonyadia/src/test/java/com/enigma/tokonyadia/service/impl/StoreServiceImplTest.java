package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Store;
import com.enigma.tokonyadia.repository.StoreRepository;
import com.enigma.tokonyadia.service.StoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreRepository storeRepository;

    private StoreService storeService;


    @BeforeEach
    void setUp() {
        storeService = new StoreServiceImpl(storeRepository);
    }

    @Test
    void getById() {
        String id = "1";

        Store store = new Store().toBuilder()
                .id(id)
                .noSiup("123123")
                .name("Toko Wibu")
                .address("Jl. Jalan")
                .mobilePhone("0810081")
                .build();

        when(storeRepository.findById(id)).thenReturn(Optional.of(store));

        Store actualStore = storeService.getById(id);

        Assertions.assertNotNull(actualStore);
    }

    @Test
    void shouldReturnStoresWhenGetAllStore() {
        List<Store> stores = Arrays.asList(
                new Store().toBuilder()
                        .id("1")
                        .noSiup("123123")
                        .name("Toserba Pasar Selasa")
                        .mobilePhone("081081")
                        .build(),
                new Store().toBuilder()
                        .id("2")
                        .noSiup("312312")
                        .name("Toserba Pasar Kamis")
                        .mobilePhone("082082")
                        .build()
        );

        when(storeRepository.findAll()).thenReturn(stores);

        List<Store> actualResults = storeService.getAll();

        assertEquals(stores, actualResults);
    }



}