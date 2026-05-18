package com.codingchallenge.ordersystem.customerorder.controller;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.*;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderProductValidationException;
import com.codingchallenge.ordersystem.customerorder.order.external.api.productcatalog.service.ProductCatalogService;
import com.codingchallenge.ordersystem.customerorder.order.service.ProductValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderProductCatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoBean
    private ProductValidationService productValidationService;

    private CreateOrderRequest request;

    @BeforeEach
    void setup() {

        OrderItemDto item = new OrderItemDto();
        item.setProductOfferingId("PROD-1");
        item.setQuantity(2);

        CustomerDto customer = new CustomerDto();
        customer.setCustomerId("CUST-1");

        SiteDto site = new SiteDto();
        site.setSiteId("SITE-1");

        PaymentMethodDto payment = new PaymentMethodDto();
        payment.setPaymentType("CARD");

        request = new CreateOrderRequest();
        request.setCategory("B2B");
        request.setCustomer(customer);
        request.setSite(site);
        request.setOrderItems(List.of(item));
        request.setPaymentMethod(payment);


        when(productCatalogService.productOfferingExists(anyString()))
                .thenReturn(true);

        doNothing().when(productValidationService)
                .validateProducts(anyList());
    }


    @Test
    void should_create_order_when_product_exists_in_catalog() throws Exception {

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }



    @Test
    void should_return_bad_request_when_product_not_found() throws Exception {

        doThrow(new OrderProductValidationException("Product validation failed"))
                .when(productValidationService)
                .validateProducts(anyList());

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void should_return_bad_request_when_product_service_fails() throws Exception {

        when(productCatalogService.productOfferingExists(anyString()))
                .thenThrow(new RuntimeException("Product service unavailable"));

        doThrow(new OrderProductValidationException("Product validation failed"))
                .when(productValidationService)
                .validateProducts(anyList());

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void should_return_bad_request_when_product_service_times_out() throws Exception {

        when(productCatalogService.productOfferingExists(anyString()))
                .thenAnswer(invocation -> {
                    Thread.sleep(3000); // simulate delay
                    return true;
                });

        doThrow(new OrderProductValidationException("timeout"))
                .when(productValidationService)
                .validateProducts(anyList());

        mockMvc.perform(post("/api/v1/customer-orders")
                        .header("Idempotency-Key", "key-4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
