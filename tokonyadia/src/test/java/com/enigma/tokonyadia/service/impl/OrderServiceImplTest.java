package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.*;
import com.enigma.tokonyadia.model.request.OrderDetailRequest;
import com.enigma.tokonyadia.model.request.OrderRequest;
import com.enigma.tokonyadia.model.response.OrderDetailResponse;
import com.enigma.tokonyadia.model.response.OrderResponse;
import com.enigma.tokonyadia.model.response.ProductResponse;
import com.enigma.tokonyadia.model.response.StoreResponse;
import com.enigma.tokonyadia.repository.OrderRepository;
import com.enigma.tokonyadia.service.CustomerService;
import com.enigma.tokonyadia.service.OrderService;
import com.enigma.tokonyadia.service.ProductPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private ProductPriceService productPriceService;
    private OrderService orderService;


    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, customerService, productPriceService);
    }

    @Test
    public void shouldReturnOrderResponseWhenCreateNewTransaction() {
        OrderRequest request = OrderRequest.builder()
                .customerId("customer-1")
                .orderDetails(List.of(
                        OrderDetailRequest.builder()
                                .productPriceId("productPrice-1")
                                .quantity(2)
                                .build()
                ))
                .build();

        Customer customer = Customer.builder()
                .id("customer-1")
                .name("rifqi")
                .mobilePhone("08123")
                .email("jl. h dahlan")
                .build();

        when(customerService.getById(anyString()))
                .thenReturn(customer);

        List<OrderDetail> orderDetails = request.getOrderDetails().stream().map(orderDetailRequest -> {
            ProductPrice productPrice = ProductPrice.builder()
                    .id("productPrice-1")
                    .price(1000L)
                    .stock(2)
                    .store(mock(Store.class))
                    .product(mock(Product.class))
                    .build();
            when(productPriceService.getById(anyString())).thenReturn(productPrice);

            return OrderDetail.builder()
                    .productPrice(productPrice)
                    .quantity(orderDetailRequest.getQuantity())
                    .build();
        }).collect(Collectors.toList());


        Order order = Order.builder()
                .customer(customer)
                .transDate(LocalDateTime.now())
                .orderDetails(orderDetails)
                .build();

        when(orderRepository.saveAndFlush(any())).thenReturn(order);
        OrderResponse actual = orderService.createNewTransaction(request);

        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {
            orderDetail.setOrder(order);

            ProductPrice currentProductPrice = orderDetail.getProductPrice();
            currentProductPrice.setStock(currentProductPrice.getStock() - orderDetail.getQuantity());

            return OrderDetailResponse.builder()
                    .orderDetailId(orderDetail.getId())
                    .quantity(orderDetail.getQuantity())
                    .product(ProductResponse.builder()
                            .id(currentProductPrice.getProduct().getId())
                            .productName(currentProductPrice.getProduct().getName())
                            .description(currentProductPrice.getProduct().getDescription())
                            .price(currentProductPrice.getPrice())
                            // TODO 8: Convert Store to storeResponse (from productPrice)
                            .store(StoreResponse.builder()
                                    .id(currentProductPrice.getStore().getId())
                                    .name(currentProductPrice.getStore().getName())
                                    .address(currentProductPrice.getStore().getAddress())
                                    .build())
                            .build())
                    .build();
        }).collect(Collectors.toList());

        assertNotNull(actual);
        assertEquals(1, orderDetailResponses.size());
    }

    @Test
    void shouldReturnOrderResponseWhenGetOrderById() {
        Product product1 = Product.builder()
                .name("baju wibu")
                .description("baju wibu")
                .build();

        Product product2 = Product.builder()
                .name("laptop wibu")
                .description("laptop wibu")
                .build();

        Store store = Store.builder()
                .name("tokopakedi")
                .mobilePhone("08123")
                .noSiup("24231412")
                .address("h. dahlan")
                .build();

        ProductPrice productPrice1 = ProductPrice.builder()
                .product(product1)
                .price(1000L)
                .stock(10)
                .store(store)
                .build();

        ProductPrice productPrice2 = ProductPrice.builder()
                .product(product2)
                .price(2000L)
                .stock(10)
                .store(store)
                .build();

        List<OrderDetail> orderDetails = List.of(OrderDetail.builder()
                        .id("order-detail-1")
                        .productPrice(productPrice1)
                        .quantity(1)
                        .build(),
                OrderDetail.builder()
                        .id("order-detail-2")
                        .productPrice(productPrice2)
                        .quantity(2)
                        .build());

        Order order = Order.builder()
                .id("order-1")
                .customer(mock(Customer.class))
                .transDate(LocalDateTime.now())
                .orderDetails(orderDetails)
                .build();
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        OrderResponse orderResponse = orderService.getOrderById("order-1");

        assertNotNull(orderResponse);
        assertEquals(2, orderResponse.getOrderDetails().size());
    }

}