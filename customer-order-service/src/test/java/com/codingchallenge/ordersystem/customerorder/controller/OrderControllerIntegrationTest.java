package com.codingchallenge.ordersystem.customerorder.controller;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.*;
import com.codingchallenge.ordersystem.customerorder.order.external.api.productcatalog.service.ProductCatalogService;
import com.codingchallenge.ordersystem.customerorder.order.service.ProductValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductValidationService productValidationService;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @BeforeEach
    void setup() {

        doNothing()
                .when(productValidationService)
                .validateProducts(anyList());

        when(productCatalogService.productOfferingExists(anyString()))
                .thenReturn(true);
    }

    // -------------------- CREATE ORDER --------------------

    @Test
    void should_create_order_successfully() throws Exception {

        CreateOrderRequest request = createValidOrderRequest();

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // -------------------- GET ORDER --------------------

    @Test
    void should_return_not_found_when_order_not_exists() throws Exception {

        mockMvc.perform(get("/api/v1/customer-orders/{id}", "invalid-id"))
                .andExpect(status().isNotFound());
    }

    // -------------------- LIST ORDERS --------------------

    @Test
    void should_list_orders_successfully() throws Exception {

        mockMvc.perform(get("/api/v1/customer-orders")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk());
    }

    // -------------------- VALIDATION TESTS --------------------

    @Test
    void should_return_bad_request_when_category_missing() throws Exception {

        CreateOrderRequest request = createValidOrderRequest();
        request.setCategory(null);

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_customer_missing() throws Exception {

        CreateOrderRequest request = createValidOrderRequest();
        request.setCustomer(null);

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_site_missing() throws Exception {

        CreateOrderRequest request = createValidOrderRequest();
        request.setSite(null);

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_order_items_empty() throws Exception {

        CreateOrderRequest request = createValidOrderRequest();
        request.setOrderItems(List.of());

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_payment_method_missing() throws Exception {

        CreateOrderRequest request = createValidOrderRequest();
        request.setPaymentMethod(null);

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_invalid_category() throws Exception {

        CreateOrderRequest request = createValidOrderRequest();
        request.setCategory("ELECTRONICS");

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -------------------- IDEMPOTENCY TEST --------------------

    @Test
    void should_handle_idempotent_requests() throws Exception {

        CreateOrderRequest request = createValidOrderRequest();
        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "same-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "same-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());

        verify(productValidationService, times(1))
                .validateProducts(anyList());
    }

    // -------------------- TEST DATA BUILDER --------------------

    private CreateOrderRequest createValidOrderRequest() {

        OrderItemDto item = new OrderItemDto();
        item.setProductOfferingId("PROD-1");
        item.setQuantity(2);

        CustomerDto customer = new CustomerDto();
        customer.setCustomerId("CUST-1");

        SiteDto site = new SiteDto();
        site.setSiteId("SITE-1");

        PaymentMethodDto payment = new PaymentMethodDto();
        payment.setPaymentType("CARD");

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCategory("B2B");
        request.setCustomer(customer);
        request.setSite(site);
        request.setOrderItems(List.of(item));
        request.setPaymentMethod(payment);

        return request;
    }
}