package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Customer;
import com.enigma.tokonyadia.entity.Order;
import com.enigma.tokonyadia.entity.OrderDetail;
import com.enigma.tokonyadia.entity.ProductPrice;
import com.enigma.tokonyadia.model.request.OrderRequest;
import com.enigma.tokonyadia.model.response.*;
import com.enigma.tokonyadia.repository.OrderRepository;
import com.enigma.tokonyadia.service.CustomerService;
import com.enigma.tokonyadia.service.OrderService;
import com.enigma.tokonyadia.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductPriceService productPriceService;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public OrderResponse createNewTransaction(OrderRequest request) {
        // TODO 1: Validate customer
        Customer customer = customerService.getById(request.getCustomerId());

        // TODO 2: Convert orderDetailRequest to orderDetail
        List<OrderDetail> orderDetails = request.getOrderDetails().stream().map(orderDetailRequest -> {
            // TODO 3: Validate Product Price
            ProductPrice productPrice = productPriceService.getById(orderDetailRequest.getProductPriceId());

            return OrderDetail.builder()
                    .productPrice(productPrice)
                    .quantity(orderDetailRequest.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        // TODO 4: Create new order
        Order order = Order.builder()
                .customer(customer)
                .transDate(LocalDateTime.now())
                .orderDetails(orderDetails)
                .build();
        orderRepository.saveAndFlush(order);

        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {
            // TODO 5: Set order from orderDetail after creating new order
            orderDetail.setOrder(order);

            // TODO 6: Change the stock from the purchased quantity
            ProductPrice currentProductPrice = orderDetail.getProductPrice();
            currentProductPrice.setStock(currentProductPrice.getStock() - orderDetail.getQuantity());

            return OrderDetailResponse.builder()
                    .orderDetailId(orderDetail.getId())
                    .quantity(orderDetail.getQuantity())
                    // TODO 7: Convert product to productResponse (from productPrice)
                    .product(ProductResponse.builder()
                            .id(currentProductPrice.getProduct().getId())
                            .productName(currentProductPrice.getProduct().getName())
                            .description(currentProductPrice.getProduct().getDescription())
                            .price(currentProductPrice.getPrice())
                            .stock(currentProductPrice.getStock())
                            // TODO 8: Convert Store to storeResponse (from productPrice)
                            .store(StoreResponse.builder()
                                    .id(currentProductPrice.getStore().getId())
                                    .name(currentProductPrice.getStore().getName())
                                    .address(currentProductPrice.getStore().getAddress())
                                    .build())
                            .build())
                    .build();
        }).collect(Collectors.toList());

        // TODO 9: Convert customer to customerResponse
        CustomerResponse customerResponse = CustomerResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .build();

        // TODO 10: Convert orderDetail to orderDetailResponse
        return OrderResponse.builder()
                .orderId(order.getId())
                .customer(customerResponse)
                .transDate(order.getTransDate())
                .orderDetails(orderDetailResponses)
                .build();
    }

    @Override
    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));

        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {
            orderDetail.setOrder(order);
            ProductPrice currentProductPrice = orderDetail.getProductPrice();

            return OrderDetailResponse.builder()
                    .orderDetailId(orderDetail.getId())
                    .quantity(orderDetail.getQuantity())
                    .product(ProductResponse.builder()
                            .id(currentProductPrice.getProduct().getId())
                            .productName(currentProductPrice.getProduct().getName())
                            .description(currentProductPrice.getProduct().getDescription())
                            .price(currentProductPrice.getPrice())
                            .stock(currentProductPrice.getStock())
                            .store(StoreResponse.builder()
                                    .id(currentProductPrice.getStore().getId())
                                    .name(currentProductPrice.getStore().getName())
                                    .address(currentProductPrice.getStore().getAddress())
                                    .build())
                            .build())
                    .build();
        }).collect(Collectors.toList());

        Customer customer = order.getCustomer();
        CustomerResponse customerResponse = CustomerResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .build();

        return OrderResponse.builder()
                .orderId(order.getId())
                .customer(customerResponse)
                .transDate(order.getTransDate())
                .orderDetails(orderDetailResponses)
                .build();
    }

    @Override
    public List<OrderResponse> getAllTransaction(String customerName) {
        Specification<Order> specification = (root, query, criteriaBuilder) -> {
            Join<Order, Customer> customer = root.join("customer");
            if (customerName != null) {
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(customer.get("name")), customerName.toLowerCase() + "%");
                return query.where(name).getRestriction();
            }

            return query.getRestriction();
        };
        List<Order> orders = orderRepository.findAll(specification);

        return orders.stream().map(order -> {
            List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {
                orderDetail.setOrder(order);
                ProductPrice currentProductPrice = orderDetail.getProductPrice();

                return OrderDetailResponse.builder()
                        .orderDetailId(orderDetail.getId())
                        .quantity(orderDetail.getQuantity())
                        .product(ProductResponse.builder()
                                .id(currentProductPrice.getProduct().getId())
                                .productName(currentProductPrice.getProduct().getName())
                                .description(currentProductPrice.getProduct().getDescription())
                                .price(currentProductPrice.getPrice())
                                .stock(currentProductPrice.getStock())
                                .store(StoreResponse.builder()
                                        .id(currentProductPrice.getStore().getId())
                                        .name(currentProductPrice.getStore().getName())
                                        .address(currentProductPrice.getStore().getAddress())
                                        .build())
                                .build())
                        .build();
            }).collect(Collectors.toList());

            Customer customer = order.getCustomer();
            CustomerResponse customerResponse = CustomerResponse.builder()
                    .customerId(customer.getId())
                    .name(customer.getName())
                    .build();

            return OrderResponse.builder()
                    .orderId(order.getId())
                    .customer(customerResponse)
                    .transDate(order.getTransDate())
                    .orderDetails(orderDetailResponses)
                    .build();
        }).collect(Collectors.toList());
    }
}
