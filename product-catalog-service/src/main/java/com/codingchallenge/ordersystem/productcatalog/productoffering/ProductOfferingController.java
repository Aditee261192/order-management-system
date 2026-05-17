package com.codingchallenge.ordersystem.productcatalog.productoffering;

import com.codingchallenge.ordersystem.model.CreateProductOfferingRequest;
import com.codingchallenge.ordersystem.model.ProductOfferingResponse;
import com.codingchallenge.ordersystem.productcatalog.productoffering.service.ProductOfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/api/v1/product-offerings")
@Tag(name = "Product Offerings API", description = "Product Offerings ")
public class ProductOfferingController {

    private final ProductOfferingService productOfferingService;

    @Autowired
    public ProductOfferingController(ProductOfferingService productOfferingService) {
        this.productOfferingService = productOfferingService;
    }

    @PostMapping
    @Operation(summary = "Create product offering.", description = "Create product offering.")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "201", description = "Product Offerings created."),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            })
    public ResponseEntity<ProductOfferingResponse> createProductOffering(
            @Valid @RequestBody CreateProductOfferingRequest request) {

        ProductOfferingResponse response = productOfferingService.createProductOfferings(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Get a list of all available product offerings.", description = "Get already persisted product offerings.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Product Offerings found.")})
    public ResponseEntity<List<ProductOfferingResponse>> getAvailableProductOfferings() {

        return
                ResponseEntity.ok(productOfferingService.getAllProductOfferings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product offering from Id .", description = "Get already persisted product offering.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product offering found"),
            @ApiResponse(responseCode = "404", description = "Product offering not found")
    })
    public ResponseEntity<ProductOfferingResponse> getProductOfferingResponseById(@PathVariable @NotBlank String id) {

        return
                ResponseEntity.ok(productOfferingService.getProductOfferingResponseById(id));

    }
}
