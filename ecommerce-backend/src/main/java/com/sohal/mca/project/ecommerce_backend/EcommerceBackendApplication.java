package com.sohal.mca.project.ecommerce_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-23
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: Main application class for the e-commerce backend.
 * This class serves as the entry point for the Spring Boot application.
 * It initializes the application context and starts the embedded server.
 * The application is configured to connect to a MySQL database and use JPA for ORM.
 * The application properties are defined in the application.properties file.
 * This class can be extended to include additional configurations or components as needed.
 */

@SpringBootApplication
public class EcommerceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceBackendApplication.class, args);
	}

}
