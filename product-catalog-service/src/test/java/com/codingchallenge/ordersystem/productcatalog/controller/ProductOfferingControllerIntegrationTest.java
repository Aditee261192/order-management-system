package com.codingchallenge.ordersystem.productcatalog.controller;

import com.codingchallenge.ordersystem.model.CreateProductOfferingRequest;
import com.codingchallenge.ordersystem.model.ProductOfferingResponse;
import com.codingchallenge.ordersystem.productcatalog.productoffering.exception.InvalidProductOfferingException;
import com.codingchallenge.ordersystem.productcatalog.productoffering.exception.ProductOfferingNotFound;
import com.codingchallenge.ordersystem.productcatalog.productoffering.service.ProductOfferingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductOfferingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductOfferingService productOfferingService;


    @Test
    void should_create_product_offering_successfully() throws Exception {

        CreateProductOfferingRequest request = createRequest("Laptop", 1000.0);
        ProductOfferingResponse response = createResponse("Laptop", 1000.0);

        when(productOfferingService.createProductOfferings(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/product-offerings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1000.0));

        verify(productOfferingService, times(1))
                .createProductOfferings(any());
    }

    @Test
    void should_return_bad_request_when_name_missing() throws Exception {

        String request = createJsonRequest(null, 1000.0);

        mockMvc.perform(post("/api/v1/product-offerings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(productOfferingService, never())
                .createProductOfferings(any());
    }


    @Test
    void should_return_bad_request_for_invalid_json() throws Exception {

        mockMvc.perform(post("/api/v1/product-offerings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_unsupported_media_type_when_missing_content_type() throws Exception {

        mockMvc.perform(post("/api/v1/product-offerings")
                        .content("{\"name\":\"Laptop\",\"price\":1000}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void should_get_product_by_id_successfully() throws Exception {

        ProductOfferingResponse response = createResponse("Phone", 500.0);

        when(productOfferingService.getProductOfferingResponseById("1"))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/product-offerings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"))
                .andExpect(jsonPath("$.price").value(500.0));

        verify(productOfferingService, times(1))
                .getProductOfferingResponseById("1");
    }


    @Test
    void should_return_404_when_product_not_found() throws Exception {

        when(productOfferingService.getProductOfferingResponseById("999"))
                .thenThrow(new ProductOfferingNotFound("not found"));

        mockMvc.perform(get("/api/v1/product-offerings/999"))
                .andExpect(status().isNotFound());

        verify(productOfferingService, times(1))
                .getProductOfferingResponseById("999");
    }

    @Test
    void should_return_bad_request_when_id_blank() throws Exception {

        mockMvc.perform(get("/api/v1/product-offerings/ "))
                .andExpect(status().isBadRequest());

        verify(productOfferingService, never())
                .getProductOfferingResponseById(anyString());
    }

    @Test
    void should_get_all_product_offerings() throws Exception {

        ProductOfferingResponse p1 = createResponse("Laptop", 1000.0);
        ProductOfferingResponse p2 = createResponse("Phone", 500.0);

        when(productOfferingService.getAllProductOfferings())
                .thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/v1/product-offerings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(productOfferingService, times(1))
                .getAllProductOfferings();
    }

    @Test
    void should_return_empty_list_when_no_products() throws Exception {

        when(productOfferingService.getAllProductOfferings())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/product-offerings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void should_map_invalid_exception_to_bad_request() throws Exception {

        when(productOfferingService.createProductOfferings(any()))
                .thenThrow(new InvalidProductOfferingException("invalid"));

        CreateProductOfferingRequest request = createRequest("", 100.0);

        mockMvc.perform(post("/api/v1/product-offerings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void should_return_405_for_invalid_method() throws Exception {

        mockMvc.perform(put("/api/v1/product-offerings"))
                .andExpect(status().isMethodNotAllowed());
    }


    private CreateProductOfferingRequest createRequest(String name, Double price) {
        CreateProductOfferingRequest request = new CreateProductOfferingRequest();
        request.setName(name);
        request.setPrice(price);
        return request;
    }

    private ProductOfferingResponse createResponse(String name, Double price) {
        ProductOfferingResponse response = new ProductOfferingResponse();
        response.setName(name);
        response.setPrice(price);
        return response;
    }

    private String createJsonRequest(String name, Double price) throws Exception {
        CreateProductOfferingRequest request = createRequest(name, price);
        return objectMapper.writeValueAsString(request);
    }

    private String invalidJson() {
        return "{ name: , price: }";
    }
}