package com.codingchallenge.ordersystem.customerorder.order;

import com.codingchallenge.ordersystem.customerorder.order.dto.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.OrderResponse;
import com.codingchallenge.ordersystem.customerorder.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1")
@Tag(name = "Currency Exchange Rate API", description = "Exchange rate for EUR ")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;

    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader(value = "Idempotency-Key", required = false)
                                                     String idempotencyKey, @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response =
                orderService.createOrder(request, idempotencyKey);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
