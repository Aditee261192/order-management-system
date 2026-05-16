package com.codingchallenge.ordersystem.customerorder.order.dao;

import com.codingchallenge.ordersystem.customerorder.order.entity.OrderIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderIdempotencyRepository extends JpaRepository<OrderIdempotency, Long> {

    Optional<OrderIdempotency> findByIdempotencyKey(String idempotencyKey);
}
