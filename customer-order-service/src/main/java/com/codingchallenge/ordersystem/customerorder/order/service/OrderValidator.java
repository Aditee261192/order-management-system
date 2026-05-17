package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.entity.Category;
import com.codingchallenge.ordersystem.customerorder.order.entity.Order;
import com.codingchallenge.ordersystem.customerorder.order.entity.State;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderStateTransitionException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

    public void validateCreateRequest(CreateOrderRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new OrderStateTransitionException("Category is required");
        }

        if (request.getCustomer() == null || request.getSite() == null) {
            throw new OrderStateTransitionException("Customer and Site are required");
        }

        Category.valueOf(request.getCategory());
    }

    public void validatePatchRequest(Order order, JsonNode patch) {

        if (patch == null || patch.isEmpty()) {
            throw new OrderStateTransitionException("Patch cannot be empty");
        }

        if (order.getState() == State.CONFIRMED) {
            throw new OrderStateTransitionException("Confirmed order cannot be modified");
        }

        if (order.getState() == State.SUBMITTED) {

            boolean onlyStateField =
                    patch.has("state")
                            && !patch.has("category")
                            && !patch.has("orderItems");

            if (!onlyStateField) {
                throw new OrderStateTransitionException(
                        "Submitted order can only update state"
                );
            }
        }
    }

    public void validateCategoryUpdate(JsonNode categoryNode) {

        if (categoryNode == null || categoryNode.isNull()) {
            throw new OrderStateTransitionException("Category cannot be null");
        }

        String categoryStr = categoryNode.asText();

        if (categoryStr.isBlank()) {
            throw new OrderStateTransitionException("Category cannot be empty");
        }

        try {
            Category.valueOf(categoryStr);
        } catch (IllegalArgumentException ex) {
            throw new OrderStateTransitionException("Invalid category: " + categoryStr);
        }
    }
}


