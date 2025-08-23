package com.sohal.mca.project.ecommerce_backend.repository;

import com.sohal.mca.project.ecommerce_backend.model.InteractionType;
import com.sohal.mca.project.ecommerce_backend.model.Product;
import com.sohal.mca.project.ecommerce_backend.model.User;
import com.sohal.mca.project.ecommerce_backend.model.UserInteraction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: UserInteractionRepository interface for the e-commerce application.
 * Provides methods to perform CRUD operations on UserInteraction entities.
 * This interface can be extended to include custom query methods as needed.
 */

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

   List<UserInteraction> findByUser(User user);

   List<UserInteraction> findByProduct(Product product);

   List<UserInteraction> findByUserAndProduct(User user, Product product);

   List<UserInteraction> findByUserAndInteractionType(User user, InteractionType interactionType);

}
