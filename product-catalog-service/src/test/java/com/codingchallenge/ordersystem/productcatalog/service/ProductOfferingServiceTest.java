package com.codingchallenge.ordersystem.productcatalog.service;

import com.codingchallenge.ordersystem.model.CreateProductOfferingRequest;
import com.codingchallenge.ordersystem.model.ProductOfferingResponse;
import com.codingchallenge.ordersystem.productcatalog.productoffering.dao.ProductOfferingRepository;
import com.codingchallenge.ordersystem.productcatalog.productoffering.entity.ProductOffering;
import com.codingchallenge.ordersystem.productcatalog.productoffering.exception.InvalidProductOfferingException;
import com.codingchallenge.ordersystem.productcatalog.productoffering.exception.ProductOfferingNotFound;
import com.codingchallenge.ordersystem.productcatalog.productoffering.service.DefaultProductOfferingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultProductOfferingServiceTest {

    @Mock
    private ProductOfferingRepository productOfferingRepository;

    @Mock
    private ModelMapper modelMapper;

    private DefaultProductOfferingService createService() {
        return new DefaultProductOfferingService(productOfferingRepository, modelMapper);
    }

    @Test
    void should_create_product_offering_successfully() {

        CreateProductOfferingRequest request = createRequest("Laptop", BigDecimal.valueOf(1000.0));

        ProductOffering savedEntity = createEntity("Laptop", BigDecimal.valueOf(1000.0));
        ProductOfferingResponse response = createResponse("Laptop", BigDecimal.valueOf(1000.0));

        when(productOfferingRepository.save(Mockito.any(ProductOffering.class)))
                .thenReturn(savedEntity);

        when(modelMapper.map(savedEntity, ProductOfferingResponse.class))
                .thenReturn(response);

        DefaultProductOfferingService service = createService();

        ProductOfferingResponse result = service.createProductOfferings(request);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(BigDecimal.valueOf(1000.0), result.getPrice());

        verify(productOfferingRepository, times(1)).save(Mockito.any());
    }

    @Test
    void should_throw_exception_when_request_is_null() {

        DefaultProductOfferingService service = createService();

        InvalidProductOfferingException ex =
                assertThrows(InvalidProductOfferingException.class,
                        () -> service.createProductOfferings(null));

        assertEquals("Request cannot be null", ex.getMessage());
        verifyNoInteractions(productOfferingRepository);
    }

    @Test
    void should_throw_exception_when_name_is_blank() {

        CreateProductOfferingRequest request = createRequest(" ", BigDecimal.valueOf(1000.0));

        DefaultProductOfferingService service = createService();

        InvalidProductOfferingException ex =
                assertThrows(InvalidProductOfferingException.class,
                        () -> service.createProductOfferings(request));

        assertEquals("Product offering name is required", ex.getMessage());
        verifyNoInteractions(productOfferingRepository);
    }

    @Test
    void should_throw_exception_when_price_is_negative() {

        CreateProductOfferingRequest request = createRequest("Laptop", BigDecimal.valueOf(-10.0));

        DefaultProductOfferingService service = createService();

        InvalidProductOfferingException ex =
                assertThrows(InvalidProductOfferingException.class,
                        () -> service.createProductOfferings(request));

        assertEquals("Price must be greater than or equal to 0", ex.getMessage());
        verifyNoInteractions(productOfferingRepository);
    }

    @Test
    void should_return_product_offering_by_id() {

        ProductOffering entity = createEntity("Laptop", BigDecimal.valueOf(1000.0));
        ProductOfferingResponse response = createResponse("Laptop", BigDecimal.valueOf(1000.0));

        when(productOfferingRepository.findById("1"))
                .thenReturn(Optional.of(entity));

        when(modelMapper.map(entity, ProductOfferingResponse.class))
                .thenReturn(response);

        DefaultProductOfferingService service = createService();

        ProductOfferingResponse result = service.getProductOfferingResponseById("1");

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
    }

    @Test
    void should_throw_exception_when_product_not_found() {

        when(productOfferingRepository.findById("1"))
                .thenReturn(Optional.empty());

        DefaultProductOfferingService service = createService();

        assertThrows(ProductOfferingNotFound.class,
                () -> service.getProductOfferingResponseById("1"));
    }

    @Test
    void should_throw_exception_when_id_is_blank() {

        DefaultProductOfferingService service = createService();

        assertThrows(InvalidProductOfferingException.class,
                () -> service.getProductOfferingResponseById(" "));
    }


    private CreateProductOfferingRequest createRequest(String name, BigDecimal price) {
        CreateProductOfferingRequest request = new CreateProductOfferingRequest();
        request.setName(name);
        request.setPrice(price);
        return request;
    }

    private ProductOffering createEntity(String name, BigDecimal price) {
        return ProductOffering.builder()
                .name(name)
                .price(price)
                .build();
    }

    private ProductOfferingResponse createResponse(String name, BigDecimal price) {
        ProductOfferingResponse response = new ProductOfferingResponse();
        response.setName(name);
        response.setPrice(price);
        return response;
    }
}
