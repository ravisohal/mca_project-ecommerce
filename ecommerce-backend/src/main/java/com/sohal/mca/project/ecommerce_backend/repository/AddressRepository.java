package com.sohal.mca.project.ecommerce_backend.repository;

import com.sohal.mca.project.ecommerce_backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: AddressRepository interface for the e-commerce application.
 * Provides methods to perform CRUD operations on Address entities.
 * This interface extends JpaRepository to leverage Spring Data JPA features.
 * It allows for easy interaction with the database without the need for boilerplate code.
 * The Address entity represents user addresses in the e-commerce system.
 * It includes methods for saving, deleting, and finding addresses.
 */

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByCity(String city);

    List<Address> findByState(String state);

    List<Address> findByCountry(String country);

}
