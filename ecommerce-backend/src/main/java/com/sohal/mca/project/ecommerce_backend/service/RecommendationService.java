package com.sohal.mca.project.ecommerce_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sohal.mca.project.ecommerce_backend.controller.RecommendationController;
import com.sohal.mca.project.ecommerce_backend.model.Product;
import java.util.List;

/**
 * Author: Ravi Sohal
 * Date: 2025-08-15
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This service handles product recommendation logic in the
 * e-commerce backend.
 * It interacts with external APIs and internal data sources to generate
 * personalized product recommendations for users.
 */
@Service    
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    ApplicationContext applicationContext;

    String recommendationServiceEndpoint = applicationContext.getEnvironment().getProperty("recommendation.service.endpoint");
    String recommendationUrl = recommendationServiceEndpoint + "/recommend?userId=";

    /**
     * Get personalized product recommendations for a specific user.
     *
     * @param userId The ID of the user for whom to retrieve recommendations.
     * @return A list of recommended products.
     */
    @Autowired
    public List<T> getRecommendationsForUser(String userId) {
        String url = recommendationUrl + userId;
        Object recommendations = restTemplate.getForObject(url, Object.class);
        return List.of(recommendations); 
    }

}
