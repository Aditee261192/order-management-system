package com.codingchallenge.ordersystem.productcatalog.productoffering.service;

import com.codingchallenge.ordersystem.model.CreateProductOfferingRequest;
import com.codingchallenge.ordersystem.model.ProductOfferingResponse;
import com.codingchallenge.ordersystem.productcatalog.productoffering.dao.ProductOfferingRepository;
import com.codingchallenge.ordersystem.productcatalog.productoffering.entity.ProductOffering;
import com.codingchallenge.ordersystem.productcatalog.productoffering.exception.InvalidProductOfferingException;
import com.codingchallenge.ordersystem.productcatalog.productoffering.exception.ProductOfferingNotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DefaultProductOfferingService implements ProductOfferingService {

    private final ProductOfferingRepository productOfferingRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public DefaultProductOfferingService(ProductOfferingRepository productOfferingRepository,
                                         ModelMapper modelMapper) {
        this.productOfferingRepository = productOfferingRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductOfferingResponse createProductOfferings(CreateProductOfferingRequest createProductOfferingRequest) {

        validateCreateRequest(createProductOfferingRequest);
        ProductOffering productOffering = ProductOffering.builder()
                .name(createProductOfferingRequest.getName())
                .price(createProductOfferingRequest.getPrice())
                .build();
        productOffering = productOfferingRepository.save(productOffering);

        return modelMapper.map(productOffering, ProductOfferingResponse.class);
    }

    @Override
    public List<ProductOfferingResponse> getAllProductOfferings() {

        return
                productOfferingRepository.findAll().stream()
                        .map(offering ->
                                modelMapper.map(offering, ProductOfferingResponse.class))
                        .toList();

    }

    @Override
    public ProductOfferingResponse getProductOfferingResponseById(String id) {

        validateId(id);

        ProductOffering productOffering = productOfferingRepository.findById(id)
                .orElseThrow(() ->
                        new ProductOfferingNotFound("Product Offering not found with id: " + id));
        return modelMapper.map(productOffering, ProductOfferingResponse.class);
    }

    private void validateCreateRequest(CreateProductOfferingRequest request) {

        if (request == null) {
            throw new InvalidProductOfferingException("Request cannot be null");
        }
        if (request.getName() == null || request.getName().isBlank()) {

            throw new InvalidProductOfferingException("Product offering name is required");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {

            throw new InvalidProductOfferingException("Price must be greater than or equal to 0");
        }
    }

    private void validateId(String id) {

        if (id == null || id.isBlank()) {
            throw new InvalidProductOfferingException("Product offering id cannot be null or empty");
        }
    }
}
