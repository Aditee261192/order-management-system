package com.codingchallenge.ordersystem.customerorder.order.service;

import com.codingchallenge.ordersystem.customerorder.order.entity.Order;
import com.codingchallenge.ordersystem.customerorder.order.entity.State;
import com.codingchallenge.ordersystem.customerorder.order.exception.OrderStateTransitionException;
import org.springframework.stereotype.Component;

@Component
public class OrderStateMachine {

    public void transition(Order order, String newStateStr) {

        if (newStateStr == null || newStateStr.isBlank()) {
            throw new OrderStateTransitionException("State cannot be empty");
        }

        final State newState;

        try {
            newState = State.valueOf(newStateStr);
        } catch (IllegalArgumentException ex) {
            throw new OrderStateTransitionException("Invalid state: " + newStateStr);
        }

        State current = order.getState();

        if (!isValidTransition(current, newState)) {
            throw new OrderStateTransitionException(
                    "Invalid transition: " + current + " → " + newState
            );
        }

        order.setState(newState);
    }

    public boolean isEditable(Order order) {
        return order.getState() != State.CONFIRMED;
    }

    public boolean isPatchRestrictedToStateOnly(Order order) {
        return order.getState() == State.SUBMITTED;
    }

    private boolean isValidTransition(State current, State next) {

        return switch (current) {
            case DRAFT -> next == State.PREVIEW;
            case PREVIEW -> next == State.DRAFT || next == State.SUBMITTED;
            case SUBMITTED -> next == State.CONFIRMED;
            case CONFIRMED -> false;
        };
    }
}
