package com.sohal.mca.project.ecommerce_backend.controller;

import com.sohal.mca.project.ecommerce_backend.model.Address;
import com.sohal.mca.project.ecommerce_backend.repository.AddressRepository;
import com.sohal.mca.project.ecommerce_backend.util.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This controller handles operations related to user addresses,
 * including retrieving all addresses, getting an address by ID, creating a new address,
 * updating an existing address, and deleting an address. It uses Swagger annotations for API documentation.
 * The controller is annotated with @RestController and @RequestMapping to define the base path for
 * address-related endpoints. The @Tag annotation is used to categorize the controller in the API documentation.
 * The controller uses a logger to log requests and responses for debugging purposes.   
 * The controller uses an AddressRepository to interact with the database for CRUD operations on addresses.
 * It provides endpoints for getting all addresses, getting an address by ID, creating a new address,
 * updating an existing address, and deleting an address. Each endpoint is secured with appropriate
 * authorization annotations to ensure that only authenticated users can perform certain actions.
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/addresses")
@Tag(name = "Addresses", description = "Operations related to user addresses")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    private final AddressRepository addressRepository; // Injecting repository directly for basic CRUD

    @Autowired
    public AddressController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
        logger.info("AddressController initialized.");
    }

    @Operation(summary = "Get all addresses", description = "Retrieve a list of all addresses. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of addresses",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Address.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view all addresses
    public ResponseEntity<List<Address>> getAllAddresses() {
        logger.debug("Received request to get all addresses.");
        List<Address> addresses = addressRepository.findAll();
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "Get address by ID", description = "Retrieve a single address by its unique ID. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Address found",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Address.class)))
    @ApiResponse(responseCode = "404", description = "Address not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view a specific address
    public ResponseEntity<Address> getAddressById(@Parameter(description = "ID of the address to retrieve") @PathVariable Long id) {
        logger.debug("Received request to get address by ID: {}", id);
        Optional<Address> address = addressRepository.findById(id);
        return address.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new address", description = "Add a new address. Requires authentication.")
    @ApiResponse(responseCode = "201", description = "Address created successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Address.class)))
    @ApiResponse(responseCode = "400", description = "Invalid address data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Only authenticated users can create addresses
    public ResponseEntity<Address> createAddress(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Address object to be created", required = true,
                                              content = @Content(schema = @Schema(implementation = Address.class, example = "{\"street\":\"789 Oak Ave\",\"city\":\"Villagetown\",\"state\":\"AB\",\"postalCode\":\"C3C3C3\",\"country\":\"Canada\"}")))
                                              @RequestBody Address address) {
        logger.info("Received request to create new address: {}", address.getStreet());
        try {
            Address createdAddress = addressRepository.save(address);
            return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating address: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update an existing address", description = "Update details of an existing address. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Address updated successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Address.class)))
    @ApiResponse(responseCode = "400", description = "Invalid address data")
    @ApiResponse(responseCode = "404", description = "Address not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can update addresses
    public ResponseEntity<Address> updateAddress(@Parameter(description = "ID of the address to update") @PathVariable Long id,
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated address object", required = true,
                                              content = @Content(schema = @Schema(implementation = Address.class, example = "{\"street\":\"789 Oak Ave Updated\",\"city\":\"Villagetown\",\"state\":\"AB\",\"postalCode\":\"C3C3C3\",\"country\":\"Canada\"}")))
                                              @RequestBody Address addressDetails) {
        logger.info("Received request to update address ID: {}", id);
        return addressRepository.findById(id)
                .map(address -> {
                    address.setStreet(addressDetails.getStreet());
                    address.setCity(addressDetails.getCity());
                    address.setState(addressDetails.getState());
                    address.setPostalCode(addressDetails.getPostalCode());
                    address.setCountry(addressDetails.getCountry());
                    Address updatedAddress = addressRepository.save(address);
                    logger.info("Address with ID {} updated.", id);
                    return ResponseEntity.ok(updatedAddress);
                })
                .orElseGet(() -> {
                    logger.warn("Address with ID {} not found for update.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Delete an address", description = "Remove an address by its ID. Requires authentication.")
    @ApiResponse(responseCode = "204", description = "Address deleted successfully")
    @ApiResponse(responseCode = "404", description = "Address not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can delete addresses
    public ResponseEntity<Void> deleteAddress(@Parameter(description = "ID of the address to delete") @PathVariable Long id) {
        logger.info("Received request to delete address ID: {}", id);
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById(id);
            logger.info("Address with ID {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Address with ID {} not found for deletion.", id);
            return ResponseEntity.notFound().build();
        }
    }
}
