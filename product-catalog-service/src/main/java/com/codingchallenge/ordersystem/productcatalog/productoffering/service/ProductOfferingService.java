package com.codingchallenge.ordersystem.productcatalog.productoffering.service;

import com.codingchallenge.ordersystem.model.CreateProductOfferingRequest;
import com.codingchallenge.ordersystem.model.ProductOfferingResponse;

import java.util.List;
import java.util.Optional;

public interface ProductOfferingService {

    ProductOfferingResponse createProductOfferings(CreateProductOfferingRequest createProductOfferingRequest);

    List<ProductOfferingResponse> getAllProductOfferings();

    ProductOfferingResponse getProductOfferingResponseById(String id);

}
