package com.codingchallenge.ordersystem.productcatalog.productoffering.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

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
    @Min(0)
    private BigDecimal price;
}
