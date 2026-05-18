package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.OrderItemDto;
import com.codingchallenge.ordersystem.customerorder.order.entity.Order;
import com.codingchallenge.ordersystem.customerorder.order.entity.OrderItem;
import com.codingchallenge.ordersystem.customerorder.order.exception.InvalidProductIdException;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderProductValidationException;
import com.codingchallenge.ordersystem.customerorder.order.external.api.productcatalog.service.ProductCatalogService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemService {

    private final ProductCatalogService productCatalogService;

    public OrderItemService(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    public List<OrderItem> buildItems(List<OrderItemDto> dtos, Order order) {

        if (dtos == null || dtos.isEmpty()) {
            throw new OrderProductValidationException("Order must contain items");
        }

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemDto dto : dtos) {

            validateProduct(dto.getProductOfferingId());
            validateQuantity(dto.getQuantity());

            items.add(OrderItem.builder()
                    .productOfferingId(dto.getProductOfferingId())
                    .quantity(dto.getQuantity())
                    .order(order)
                    .build());
        }

        return items;
    }

    public List<OrderItem> replaceItems(JsonNode itemsNode, Order order) {

        if (itemsNode == null || !itemsNode.isArray()) {
            throw new OrderProductValidationException("orderItems must be an array");
        }

        List<OrderItem> items = new ArrayList<>();

        for (JsonNode node : itemsNode) {

            String productId = node.get("productOfferingId").asText();

            validateProduct(productId);

            int quantity = node.get("quantity").asInt();

            validateQuantity(quantity);

            items.add(OrderItem.builder()
                    .productOfferingId(productId)
                    .quantity(quantity)
                    .order(order)
                    .build());
        }

        return items;
    }

    private void validateProduct(String productId) {

        try {
            boolean exists = productCatalogService.productOfferingExists(productId);

            if (!exists) {
                throw new OrderProductValidationException("Invalid product: " + productId);
            }

        } catch (WebClientResponseException ex) {
            throw new InvalidProductIdException(
                    "Product service error for productId: " + productId
            );
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new OrderProductValidationException("Quantity must be > 0");
        }
    }
}
