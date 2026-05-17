package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.external.api.productcatalog.service.ProductCatalogService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class ProductCatalogClient {

    private final ProductCatalogService productCatalogService;

    public ProductCatalogClient(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @CircuitBreaker(name = "productCatalog", fallbackMethod = "fallbackProductCheck")
    public boolean isProductValid(String productId) {
        return productCatalogService.productOfferingExists(productId);
    }

    public boolean fallbackProductCheck(String productId, Throwable ex) {
        throw new RuntimeException(
                "Product catalog temporarily unavailable for productId: " + productId,
                ex
        );
    }
}
