# E-commerce Platform with AI-Powered Recommendation System

### Project Summary

This project is an e-commerce platform developed for the MCA 3rd semester, with a core focus on implementing a personalized, **AI-driven recommendation engine** to enhance the user shopping experience. The system is designed as a simplified e-commerce website where users can browse products, manage a shopping cart, and complete a simulated checkout process. The unique feature is the intelligent recommendation engine, which analyzes user behavior to suggest relevant products. The platform also includes a comprehensive admin module for managing products, categories, orders, and user data.

---

### Key Features

* **User Authentication & Management:** Secure registration, login, and profile management for both customers and admins.
* **Product Catalog:** A browsable and searchable product catalog with detailed product information.
* **Shopping Cart:** Users can add, update, and remove products from their cart.
* **Order Management:** A simulated checkout process and a history of all placed orders.
* **Admin Dashboard:** Admins can view shop statistics, monitor low-stock items, and manage products, categories, and users.
* **AI-Powered Recommendations:** A personalized recommendation engine that suggests products based on user views and purchase history, addressing the **cold start problem** by suggesting popular items to new users.
* **Responsive Web Interface:** A clean, intuitive, and responsive user interface built for optimal viewing on various devices.

---

### High-Level Architecture

The system is built on a **modular architecture** composed of three main components that communicate via APIs.

* **Frontend UI:** A web portal developed using **Angular 20** on **Node.js**. It handles all user interactions and communicates with the backend via REST APIs.
* **Backend Core:** A set of **REST APIs** built with the **Java Spring Boot framework** that manages all e-commerce business logic, data persistence, and security.
* **Recommendation Engine:** A separate service developed in the **R language** using the **`plumber` library** to expose a machine learning model as an API. This service analyzes `UserInteraction` data from the database to generate personalized recommendations.

The entire system relies on a **MySQL database** for data storage, including products, user profiles, orders, and user interaction logs.



---

### Technology Stack

* **Frontend:**
    * **Framework:** Angular 20 on Node.js
    * **Languages:** HTML, CSS, JavaScript, TypeScript
    * **Styling:** Tailwind CSS
    * **Dependencies:** JQuery, `fetch` API or `axios` for HTTP requests

* **Backend:**
    * **Language:** Java
    * **Framework:** Spring Boot
    * **ORM:** Spring Data JPA / Hibernate
    * **Build Tool:** Maven
    * **Security:** Spring Security for authentication and authorization.

* **Recommendation Engine:**
    * **Language:** R
    * **Libraries:** `recommenderlab`, `plumber`, `reshape2`, `DBI`, `RMySQL`

* **Database:**
    * **Type:** MySQL
    * **Driver:** JDBC Driver

* **Tools:**
    * **Development IDE:** Visual Studio Code, R Studio
    * **Version Control:** Git
    * **Repository:** `https://github.com/ravisohal/mca_project-ecommerce.git`

---

### Setup and Installation

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/ravisohal/mca_project-ecommerce.git](https://github.com/ravisohal/mca_project-ecommerce.git)
    cd mca_project-ecommerce
    ```

2.  **Database Setup:**
    * Install MySQL and create a database named `mca_ecom_project_sqldb`.
    * Import the provided SQL schema file located at `mca_ecom_project_sqlb-complete.sql` to set up the tables.

3.  **Backend (Java Spring Boot):**
    * Navigate to the backend project directory.
    * Configure your `application.properties` or `application.yml` with your MySQL database credentials.
    * Use Maven to build and run the application.
    * `mvn spring-boot:run`

4.  **Recommendation Engine (R):**
    * Open the R project in RStudio.
    * Install all required libraries: `install.packages(c("recommenderlab", "plumber", "reshape2", "DBI", "RMySQL"))`
    * Run the script that exposes the recommendation model as a web API using the `plumber` package.

5.  **Frontend (Angular):**
    * Navigate to the frontend project directory.
    * Install dependencies: `npm install`
    * Start the development server: `ng serve`
    * Access the application at `http://localhost:4200`.

---

### Challenges and Solutions

* **Data Sparsity:** This was simulated by generating a sufficient number of `user_interactions` to make the recommendation model meaningful.
* **Cold Start Problem:** For new users with no interaction history, the recommendation engine defaults to suggesting a list of the most popular products, ensuring an immediate and relevant experience.
* **Integration:** The communication between the Java backend and the R ML service is managed through **RESTful APIs**, ensuring smooth data transfer and modularity.
* **Security:** **Spring Security** is implemented in the backend to manage user authentication and role-based access control, protecting API endpoints based on user roles (Admin, Customer, Public).