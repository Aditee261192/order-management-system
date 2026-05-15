package com.codingchallenge.ordersystem.productcatalog.productoffering.service;

import com.codingchallenge.ordersystem.model.CreateProductOfferingRequest;
import com.codingchallenge.ordersystem.model.ProductOfferingResponse;
import com.codingchallenge.ordersystem.productcatalog.productoffering.dao.ProductOfferingRepository;
import com.codingchallenge.ordersystem.productcatalog.productoffering.entity.ProductOffering;
import com.codingchallenge.ordersystem.productcatalog.productoffering.exception.ProductOfferingNotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        ProductOffering productOffering=ProductOffering.builder()
                        .name(createProductOfferingRequest.getName())
                        .price(createProductOfferingRequest.getPrice())
                        .build();
        productOffering=productOfferingRepository.save(productOffering);

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


         return Optional.ofNullable(
                        productOfferingRepository.findById(id)
                )
                .map(offering -> modelMapper.map(offering,ProductOfferingResponse.class))
                .orElseThrow(() -> new ProductOfferingNotFound("Product Offering not found.")
                );



    }
}
