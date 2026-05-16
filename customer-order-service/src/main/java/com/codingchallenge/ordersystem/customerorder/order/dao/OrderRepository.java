package com.codingchallenge.ordersystem.customerorder.order.dao;

import com.codingchallenge.ordersystem.customerorder.order.entity.Category;
import com.codingchallenge.ordersystem.customerorder.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Page<Order> findByCategory(Category category, Pageable pageable);

    Page<Order> findAll(Pageable pageable);
}
