package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.entity.Customer;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.security.UserSecurity;
import com.enigma.tokonyadia.service.CustomerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.security.sasl.AuthenticationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {


    @MockBean
    private CustomerService customerService;

    @MockBean
    private UserSecurity userSecurity;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }


    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void getAllCustomer() throws Exception{
        List<Customer> customers = new ArrayList<>();
        customers.add(Customer.builder()
                .id("1")
                .name("edy")
                .mobilePhone("12345")
                .email("edy@gmail.com")
                .build());
        customers.add(Customer.builder()
                .id("2")
                .name("azhar")
                .mobilePhone("1235")
                .email("azhar@gmail.com")
                .build());

        when(customerService.searchByNameOrPhoneOrEmail(null, null, null)).thenReturn(customers);

        CommonResponse<List<Customer>> response = CommonResponse.<List<Customer>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully get all customer")
                .data(customers)
                .build();

        mockMvc.perform(get("/api/v1/customers")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    CommonResponse<List<Customer>> actual = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
                    Assertions.assertEquals(response.getStatusCode(), actual.getStatusCode());
                    Assertions.assertEquals(response.getData(), actual.getData());
                });
    }


    @WithMockUser(username = "edy")
    @Test
    void itShouldHava200StatusCodeAndValidResponseWhenGetById() throws  Exception{
        String id= "edy-1";
        Customer customer = Customer.builder()
                .id(id)
                .name("edy")
                .mobilePhone("08123")
                .build();
        when(customerService.getById(anyString())).thenReturn(customer);

        CommonResponse<Customer> response = CommonResponse.<Customer>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully get customer by id")
                .data(customer)
                .build();

        mockMvc.perform(get("/api/v1/customers/" + id).contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    CommonResponse<Customer> actual = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    Assertions.assertEquals(response.getStatusCode(), actual.getStatusCode());
                    Assertions.assertEquals(response.getData(), actual.getData());
                });
    }

    @WithMockUser(username = "Cilox", roles = "CUSTOMER")
    @Test
    void updateCustomer() {

    }

    @WithMockUser(username = "Cilox", roles = "CUSTOMER")
    @Test
    void deleteCustomer() throws Exception {
        String customerId = "cilo";
        Customer customer = Customer.builder()
                .id(customerId)
                .name("budi")
                .mobilePhone("081081")
                .build();

        when(userSecurity.checkCustomer(isA(Authentication.class), isA(String.class))).thenReturn(true);
        doNothing().when(customerService).deleteById(customerId);

        mockMvc.perform(delete("/api/v1/customers/" + customerId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    CommonResponse<Customer> actual = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });
                    Assertions.assertEquals(200, actual.getStatusCode());
                });

    }


    @WithMockUser(username = "edy", roles = "ADMIN")
    @Test
    void shouldHave403StatusCodeWhenDeleteById() throws Exception {
        String id = "edy-1";
        when(userSecurity.checkCustomer(isA(Authentication.class), isA(String.class))).thenReturn(true);
        doNothing().when(customerService).deleteById(id);

        mockMvc.perform(delete("/api/v1/customers/" + id)
                        .contentType("application/json"))
                .andExpect(status().isForbidden())
                .andDo(result -> {
                    CommonResponse<Customer> actual = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });
                    Assertions.assertEquals(403, actual.getStatusCode());
                });
    }
}