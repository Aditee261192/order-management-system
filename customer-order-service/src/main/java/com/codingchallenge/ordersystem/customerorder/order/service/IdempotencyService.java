package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.dao.OrderIdempotencyRepository;
import com.codingchallenge.ordersystem.customerorder.order.dao.OrderRepository;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.entity.Order;
import com.codingchallenge.ordersystem.customerorder.order.entity.OrderIdempotency;
import com.codingchallenge.ordersystem.customerorder.order.exception.ExistingOrderException;
import com.codingchallenge.ordersystem.customerorder.order.exception.IdempotencyConflictException;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdempotencyService {

    private final OrderIdempotencyRepository idempotencyRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(
            OrderIdempotencyRepository idempotencyRepository,
            OrderRepository orderRepository,
            ObjectMapper objectMapper
    ) {
        this.idempotencyRepository = idempotencyRepository;
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    public void checkCreateRequest(CreateOrderRequest request, String key) {

        if (key == null) return;

        Optional<OrderIdempotency> existing =
                idempotencyRepository.findByIdempotencyKey(key);

        if (existing.isEmpty()) return;

        OrderIdempotency stored = existing.get();

        String currentHash = generateHash(request);

        if (!stored.getRequestHash().equals(currentHash)) {
            throw new IdempotencyConflictException(
                    "Idempotency-Key reused with different request payload"
            );
        }

        boolean orderExists = orderRepository.existsById(stored.getOrderId());

        if (!orderExists) {
            throw new OrderNotFoundException("Order referenced by idempotency key not found");
        }

        throw new ExistingOrderException(
                "Order already exists for this idempotency key"
        );
    }

    public void saveIfRequired(CreateOrderRequest request, String key, Order order) {

        if (key == null) return;

        OrderIdempotency entry = new OrderIdempotency();
        entry.setIdempotencyKey(key);
        entry.setRequestHash(generateHash(request));
        entry.setOrderId(order.getId());

        idempotencyRepository.save(entry);
    }

    private String generateHash(CreateOrderRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            return DigestUtils.sha256Hex(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate idempotency hash", e);
        }
    }
}
