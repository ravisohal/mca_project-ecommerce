# Technical Documentation: Product Recommendation API

## 1. Introduction
This document provides a technical overview and detailed specifications for the product recommendation API. The API is built using R with the plumber framework and leverages the recommenderlab package for collaborative filtering. Its primary function is to provide real-time product recommendations to users based on their past interactions.

## 2. Architectural Overview
The architecture follows a three-tier model:

### Front-end (Client): An application (e.g., a website or mobile app) that makes a GET request to the API.

### Back-end (API Server): A plumber API that handles the request, interacts with the database, processes the data, and generates recommendations.

### Database: A MySQL database that stores user interaction data (user_id, product_id, interaction_type).

## 3. API Endpoint
The API exposes a single endpoint for generating recommendations.

**Method  Endpoint    Description**
GET	    */recommend*  Fetches a list of recommended products for a specific user.

## 4. Endpoint Specifications
**URL:** *http://<server_address>:<port>/recommend*

**Parameters:**  userId (required) - The unique identifier of the user for whom to generate recommendations.

**Request Example:** GET /recommend?userId=123

**Success Response:** A JSON object containing a list of recommended product_ids.

**Example**
[ "product_107", "product_120", "product_121", "product_124", "product_125" ]

**Error Response:** A JSON object with an error message. This occurs if the specified userId is not found in the database.

**Example**
{ "error": "User not found in data." }

## 5. Technical Workflow and Sequence Diagram
The recommendation process is a sequential flow triggered by a GET request.

### 5.1. Workflow Description:

A client application sends a GET request to the /recommend endpoint with a userId.

The plumber API receives the request.

The API connects to the MySQL database using the RMySQL library.

A query is executed to fetch all user interaction data.

The fetched data is transformed into a realRatingMatrix, a data structure required by the recommenderlab package. This involves:

Data Aggregation: The dcast function aggregates multiple interactions into a single rating per user-product pair.

Matrix Conversion: The data is reshaped into a user-item matrix where rows represent users, columns represent products, and cell values represent ratings.

Rating Normalization: The interaction_type is mapped to a numeric rating (VIEW=1, ADD_TO_CART=2, etc.).

A User-Based Collaborative Filtering (UBCF) model is trained on this matrix.

The model predicts the top 5 product recommendations for the target user.

A fallback mechanism is in place: if the UBCF model cannot generate recommendations (due to insufficient data), the API returns a list of the top 5 most popular products based on the total number of interactions.

The recommendations are converted into a JSON list and returned to the client.

### 5.2. Sequence Diagram:

## 6. Dependencies
The script relies on the following R libraries. They must be installed for the API to function correctly.

**Library	Function**
plumber	Creates the web API.
recommenderlab	Core library for building the recommendation model.
reshape2	Used for reshaping data (dcast).
DBI & RMySQL	Used for connecting to and querying the MySQL database.

## 7. Model Details
**Model:** User-Based Collaborative Filtering (UBCF).

**Rating System:** A heuristic rating system based on user_interactions. This simple mapping provides a weight for each type of interaction, allowing the model to understand user preference more accurately.

**Fallback Strategy:** To handle cold-start problems and data sparsity, the API defaults to recommending the most popular products when the UBCF model cannot find suitable recommendations. This ensures a seamless user experience by always providing a list of products.

## Badges

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Built with R](https://img.shields.io/badge/Built_with-R-blue.svg)](https://www.r-project.org/)
