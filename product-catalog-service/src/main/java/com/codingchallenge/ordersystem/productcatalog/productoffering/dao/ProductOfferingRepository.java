package com.codingchallenge.ordersystem.productcatalog.productoffering.dao;

import com.codingchallenge.ordersystem.productcatalog.productoffering.entity.ProductOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOfferingRepository extends JpaRepository<ProductOffering,String> {
}
