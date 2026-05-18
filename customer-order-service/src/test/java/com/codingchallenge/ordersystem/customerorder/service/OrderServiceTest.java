package com.codingchallenge.ordersystem.customerorder.service;

import com.codingchallenge.ordersystem.customerorder.order.dao.OrderRepository;
import com.codingchallenge.ordersystem.customerorder.order.dto.request.*;
import com.codingchallenge.ordersystem.customerorder.order.dto.response.OrderResponse;
import com.codingchallenge.ordersystem.customerorder.order.entity.Category;
import com.codingchallenge.ordersystem.customerorder.order.entity.Order;
import com.codingchallenge.ordersystem.customerorder.order.entity.State;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderNotFoundException;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderProductValidationException;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderStateTransitionException;
import com.codingchallenge.ordersystem.customerorder.order.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private OrderValidator orderValidator;
    @Mock
    private OrderStateMachine orderStateMachine;
    @Mock
    private OrderItemService orderItemService;
    @Mock
    private IdempotencyService idempotencyService;
    @Mock
    private ProductValidationService productValidationService;


    @Test
    void should_create_order_successfully() {

        CreateOrderRequest request = buildValidCreateOrderRequest();

        Order order = buildOrder("ORDER-1");

        OrderResponse response = buildOrderResponse("ORDER-1", "B2B", "DRAFT");

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(response);

        DefaultOrderService service = buildService();

        OrderResponse result = service.createOrder(request, "key-1");

        assertEquals("ORDER-1", result.getId());
        assertEquals("B2B", result.getCategory());

        verify(productValidationService).validateProducts(request.getOrderItems());
    }

    @Test
    void should_return_order_by_id() {

        Order order = buildOrder("ORDER-1");
        OrderResponse response = buildOrderResponse("ORDER-1", "B2B", "DRAFT");

        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(response);

        DefaultOrderService service = buildService();

        OrderResponse result = service.getOrderById("ORDER-1");

        assertEquals("ORDER-1", result.getId());
    }

    @Test
    void should_return_list_of_orders() {

        Order order = buildOrder("ORDER-1");
        Page<Order> page = new PageImpl<>(List.of(order));

        OrderResponse response = buildOrderResponse("ORDER-1", "B2B", "DRAFT");

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(response);

        DefaultOrderService service = buildService();

        var result = service.listOrders(10, 0, null);

        assertEquals(1, result.getItems().size());
    }

    @Test
    void should_patch_order_successfully() {

        Order order = buildOrder("ORDER-1");

        ObjectNode patch = new ObjectMapper().createObjectNode();
        patch.put("state", "SUBMITTED");

        OrderResponse response = buildOrderResponse("ORDER-1", "B2B", "SUBMITTED");

        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(response);

        DefaultOrderService service = buildService();

        OrderResponse result = service.patchOrder("ORDER-1", patch);

        assertEquals("SUBMITTED", result.getState());
        verify(orderStateMachine).transition(order, "SUBMITTED");
    }

    @Test
    void should_throw_OrderNotFoundException() {

        when(orderRepository.findById("INVALID")).thenReturn(Optional.empty());

        DefaultOrderService service = buildService();

        assertThrows(OrderNotFoundException.class,
                () -> service.getOrderById("INVALID"));
    }

    @Test
    void should_throw_ProductValidationException() {

        CreateOrderRequest request = buildValidCreateOrderRequest();

        doThrow(new OrderProductValidationException("failed"))
                .when(productValidationService)
                .validateProducts(anyList());

        DefaultOrderService service = buildService();

        assertThrows(OrderProductValidationException.class,
                () -> service.createOrder(request, "key-1"));
    }

    @Test
    void should_throw_StateTransitionException() {

        Order order = buildOrder("ORDER-1");

        ObjectNode patch = new ObjectMapper().createObjectNode();
        patch.put("category", "B2C");

        when(orderRepository.findById("ORDER-1")).thenReturn(Optional.of(order));

        doThrow(new OrderStateTransitionException("invalid"))
                .when(orderValidator)
                .validatePatchRequest(order, patch);

        DefaultOrderService service = buildService();

        assertThrows(OrderStateTransitionException.class,
                () -> service.patchOrder("ORDER-1", patch));
    }


    private DefaultOrderService buildService() {
        return new DefaultOrderService(
                orderRepository,
                modelMapper,
                orderValidator,
                orderStateMachine,
                orderItemService,
                idempotencyService,
                productValidationService
        );
    }

    private CreateOrderRequest buildValidCreateOrderRequest() {

        OrderItemDto item = new OrderItemDto();
        item.setProductOfferingId("PROD-1");
        item.setQuantity(2);

        CustomerDto customer = new CustomerDto();
        customer.setCustomerId("CUST-1");

        SiteDto site = new SiteDto();
        site.setSiteId("SITE-1");

        PaymentMethodDto payment = new PaymentMethodDto();
        payment.setPaymentType("CARD");

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCategory("B2B");
        request.setCustomer(customer);
        request.setSite(site);
        request.setOrderItems(List.of(item));
        request.setPaymentMethod(payment);

        return request;
    }

    private Order buildOrder(String id) {
        return Order.builder()
                .id(id)
                .category(Category.B2B)
                .customerId("CUST-1")
                .siteId("SITE-1")
                .state(State.DRAFT)
                .build();
    }

    private OrderResponse buildOrderResponse(String id, String category, String state) {
        return OrderResponse.builder()
                .id(id)
                .category(category)
                .state(state)
                .build();
    }
}
