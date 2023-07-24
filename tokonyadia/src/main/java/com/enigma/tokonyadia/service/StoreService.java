package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.entity.Store;
import com.enigma.tokonyadia.model.request.UpdateStoreRequest;
import com.enigma.tokonyadia.model.response.StoreResponse;

import java.util.List;

public interface StoreService {
    Store getById(String id);
    List<Store> getAll();
    StoreResponse update(UpdateStoreRequest request);

}
