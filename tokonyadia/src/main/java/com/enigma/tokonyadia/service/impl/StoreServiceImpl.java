package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Store;
import com.enigma.tokonyadia.model.request.UpdateStoreRequest;
import com.enigma.tokonyadia.model.response.StoreResponse;
import com.enigma.tokonyadia.repository.StoreRepository;
import com.enigma.tokonyadia.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    public Store getById(String id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
    }

    @Override
    public List<Store> getAll() {
        return storeRepository.findAll();
    }

    @Override
    public StoreResponse update(UpdateStoreRequest request) {
        getById(request.getStoreId());

        Store store = Store.builder()
                .id(request.getStoreId())
                .name(request.getStoreName())
                .noSiup(request.getNoSiup())
                .address(request.getAddress())
                .mobilePhone(request.getMobilePhone())
                .build();
        storeRepository.save(store);

        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .mobilePhone(store.getMobilePhone())
                .build();
    }
}
