package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.model.request.OrderRequest;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.model.response.OrderResponse;
import com.enigma.tokonyadia.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/transactions")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createNewTransaction(@RequestBody OrderRequest request) {
        OrderResponse orderResponse = orderService.createNewTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully create new transaction")
                        .data(orderResponse)
                        .build());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable String id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get transaction by id")
                        .data(order)
                        .build());
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(@RequestParam(name = "customerName", required = false) String customerName) {
        List<OrderResponse> orderResponses = orderService.getAllTransaction(customerName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get all transaction")
                        .data(orderResponses)
                        .build());
    }

}
