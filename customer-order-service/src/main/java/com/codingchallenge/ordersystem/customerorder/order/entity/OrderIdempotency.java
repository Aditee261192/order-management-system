package com.codingchallenge.ordersystem.customerorder.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "order_idempotency",
        uniqueConstraints = @UniqueConstraint(columnNames = "idempotency_key")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderIdempotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "request_hash", nullable = false)
    private String requestHash;

    @Column(name = "order_id", nullable = false)
    private String orderId;
}