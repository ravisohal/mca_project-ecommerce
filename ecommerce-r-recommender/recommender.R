# install.packages("plumber")
# install.packages("recommenderlab")
library(reshape2)
library(recommenderlab)
library(plumber)
library(DBI)
library(RMySQL)       
library(recommenderlab)

# Connect to database
con <- dbConnect(
  RMySQL::MySQL(),
  dbname = "mca_ecom_project_sqldb",
  host = "localhost",
  user = "ravisohalmysqldb",
  password = "mc@Pr0j3tEc0m"
)

#* @get /recommend
function(userId) {
  # Fetch interactions
  query <- "
SELECT 
    user_id, 
    product_id,
    CASE interaction_type
        WHEN 'VIEW' THEN 1
        WHEN 'ADD_TO_CART' THEN 2
        WHEN 'REMOVE_FROM_CART' THEN 0
        WHEN 'PURCHASE' THEN 5
        WHEN 'REVIEW' THEN 4
        ELSE 0
    END as rating
FROM user_interactions;
"
  
  data <- dbGetQuery(con, query)
  
  # Ensure the user_id and rating columns are the correct data type for row names
  data$user_id <- as.character(data$user_id)
  data$rating <- as.numeric(data$rating)
  
  # Convert to wide format: users as rows, products as columns
  rating_matrix <- dcast(data, user_id ~ product_id, 
                         value.var="rating",
                         fun.aggregate = max,
                         fill=0)
  
  # Save user ids separately
  user_ids <- rating_matrix$user_id
  
  # Remove user_id column for recommenderlab
  rating_matrix <- as.matrix(rating_matrix[,-1])
  rownames(rating_matrix) <- user_ids 
  
  # Build user-item matrix
  rating_matrix <- as(rating_matrix, "realRatingMatrix")  # needs transformation step
  
  # Train recommender
  rec <- Recommender(rating_matrix, method = "UBCF")  # User-based collaborative filtering
  
  # Ensure user exists
  target_user <- as.numeric(userId)
  target_user_index <- which(rownames(rating_matrix) == target_user)
  
  # Ensure the user exists
  if (length(target_user_index) == 0) {
    return(list(error = "User not found in data."))
  }
  
  # Get the user's ratings from the matrix
  target_user_ratings <- rating_matrix[target_user_index, , drop = FALSE]
  
  # Get top-N recommendations for the target user
  preds <- predict(rec, target_user_ratings, n = 5)
  
  recommended_items <- (as(preds, "list"))
  
  # Check if the list is empty and provide a fallback
  if (length(unlist(recommended_items)) == 0) {
    # If no recommendations, find the most popular products as a fallback
    popular_products <- head(sort(colSums(rating_matrix), decreasing = TRUE), 5)
    
    # Return the names of the top popular products
    return(list(fallback_recommendations = names(popular_products)))
  } else {
    return(recommended_items)
  }
}

