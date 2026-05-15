package com.codingchallenge.ordersystem.customerorder.order.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;


    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @NotNull
    private Category category;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "site_id", nullable = false)
    private String siteId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentMethod paymentMethod;

    /*public class CreateOrderRequest {

    @NotNull
    private Category category;

    @NotBlank
    private String customerId;

    @NotBlank
    private String siteId;

    @NotEmpty
    private List<OrderItemRequest> orderItems;

    @NotNull
    private PaymentMethodType paymentMethodType;
}*/

}
