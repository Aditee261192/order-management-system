package com.codingchallenge.ordersystem.customerorder.order.external.api.productcatalog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class ProductCatalogService {

    private final WebClient webClient;

    public ProductCatalogService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;

    public boolean productOfferingExists(String productId) {

        try {
            webClient.get()
                    .uri(productServiceBaseUrl + "/api/v1/product-offerings/{id}", productId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return true;

        } catch (WebClientResponseException.NotFound ex) {
            return false;
        }
    }
}
