package com.codingchallenge.ordersystem.customerorder.order;

import com.codingchallenge.ordersystem.customerorder.order.dto.request.CreateOrderRequest;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.OrderListResponse;
import com.codingchallenge.ordersystem.customerorder.order.dto.response.OrderResponse;
import com.codingchallenge.ordersystem.customerorder.order.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/customer-orders")
@Tag(name = "Order Management API", description = "APIs for managing customer orders")
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
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Orders retrieved successfully")})
    public ResponseEntity<OrderListResponse> listOrders(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(orderService.listOrders(limit, offset, category));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details for given Id", description = "Get already persisted order for given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {

        return
                ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/orders/{id}")
    @Operation(summary = "Update Order", description = "Update already persisted order by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> patchOrder(
            @PathVariable String id,
            @RequestBody JsonNode patch
    ) {
        return ResponseEntity.ok(orderService.patchOrder(id, patch));
    }
}
