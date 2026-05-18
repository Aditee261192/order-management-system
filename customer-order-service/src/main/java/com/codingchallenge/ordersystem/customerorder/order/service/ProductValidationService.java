package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.OrderItemDto;
import com.codingchallenge.ordersystem.customerorder.order.exception.InvalidProductIdException;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderProductValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class ProductValidationService {


    private final ProductCatalogClient productCatalogClient;
    private final Executor executor;

    public ProductValidationService(ProductCatalogClient productCatalogClient,
                                    Executor executor
    ) {
        this.productCatalogClient = productCatalogClient;
        this.executor = executor;
    }

    public void validateProducts(List<OrderItemDto> items) {

        List<CompletableFuture<Boolean>> futures = items.stream()
                .map(item -> CompletableFuture.supplyAsync(
                        () -> productCatalogClient.isProductValid(item.getProductOfferingId()),
                        executor
                ))
                .toList();

        for (CompletableFuture<Boolean> future : futures) {
            try {
                Boolean exists = future.get(10, TimeUnit.SECONDS);

                if (!Boolean.TRUE.equals(exists)) {
                    throw new OrderProductValidationException("One or more products are invalid");
                }
            }catch (Exception ex) {
               throw new InvalidProductIdException("Product Offering id is not available ");
            }
        }
    }
}