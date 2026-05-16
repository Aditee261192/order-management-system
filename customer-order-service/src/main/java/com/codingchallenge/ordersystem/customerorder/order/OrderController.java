package com.codingchallenge.ordersystem.customerorder.order;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.response.OrderResponse;
import com.codingchallenge.ordersystem.customerorder.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/customer-orders")
@Tag(name = "Currency Exchange Rate API", description = "Exchange rate for EUR ")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;

    }

    @PostMapping
    @Operation(summary = "Creates Order.", description = "Created Order")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Order created.")})
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader(value = "Idempotency-Key", required = false)
                                                     String idempotencyKey, @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response =
                orderService.createOrder(request, idempotencyKey);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(summary = "List orders ", description = "Get already persisted orders.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")})
    public ResponseEntity<List<OrderResponse>> listOrders() {

        return
                ResponseEntity.ok(orderService.listOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details for given Id", description = "Get already persisted order for given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product offering found"),
            @ApiResponse(responseCode = "404", description = "Product offering not found")
    })
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {

        return
                ResponseEntity.ok(orderService.getOrderById(id));
    }
}
