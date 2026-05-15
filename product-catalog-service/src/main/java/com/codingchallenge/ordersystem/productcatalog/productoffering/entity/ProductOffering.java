package com.codingchallenge.ordersystem.productcatalog.productoffering.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_offerings")
public class ProductOffering {

    @Id
    @GeneratedValue(generator = "CustomerIdGenerator")
    @GenericGenerator(name = "CustomerIdGenerator",
            strategy = "com.codingchallenge.ordersystem.productcatalog.productoffering.entity.CustomerIdGenerator")
    @Column(name = "id")
    private String offeringId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;
}
