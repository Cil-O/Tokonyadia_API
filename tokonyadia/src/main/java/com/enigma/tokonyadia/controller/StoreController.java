package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.entity.Store;
import com.enigma.tokonyadia.model.request.UpdateStoreRequest;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.model.response.StoreResponse;
import com.enigma.tokonyadia.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/store")
public class StoreController {
    private final StoreService storeService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getStoreById(@PathVariable String id) {
        Store store = storeService.getById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get store by id")
                        .data(store)
                        .build());
    }

    @GetMapping
    public ResponseEntity<?> getAllStore() {
        List<Store> stores = storeService.getAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get all store")
                        .data(stores)
                        .build());
    }

    @PreAuthorize("hasRole('SELLER') and @userSecurity.checkSeller(authentication, #request.getStoreId())")
    @PutMapping
    public ResponseEntity<?> updateStore(@RequestBody UpdateStoreRequest request) {
        StoreResponse storeResponse = storeService.update(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully update store")
                        .data(storeResponse)
                        .build());
    }

}
