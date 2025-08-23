package com.sohal.mca.project.ecommerce_backend.util;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-27
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: Utility class to hold application-wide constants,
 * particularly API versioning. These are compile-time constants
 * suitable for use in annotations like @RequestMapping.
 */

public final class ApiConstants {

    // Define API_VERSION as a true compile-time constant
    // Changed "v1.0" to "1.0" to match controller @RequestMapping
    public static final String API_VERSION = "1.0";

    // Base path for API, which is also a compile-time constant
    public static final String API_BASE_PATH = "/api/";

    // Full base path for controllers, now a compile-time constant
    // This will now correctly resolve to "/api/1.0"
    public static final String API_V1_BASE_PATH = API_BASE_PATH + API_VERSION;

    // Private constructor to prevent instantiation
    private ApiConstants() {
        // restrict instantiation
    }
}
