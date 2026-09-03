# ShopKart

ShopKart is a Spring Boot REST API for managing products and categories.

The application provides CRUD operations, product searching, filtering, pagination, sorting, validation, exception handling, Swagger/OpenAPI documentation, and automated testing.

## Technologies Used

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- MySQL
- Jakarta Bean Validation
- Swagger / OpenAPI
- JUnit
- Mockito
- Maven
- JaCoCo

## Features

### Product Management

- Create a product
- Get product by ID
- Get all products
- Update a product
- Delete a product
- Search products by name
- Filter products by price and quantity
- Filter products by category
- Pagination
- Sorting by ID, name, price, and quantity
- Ascending and descending sorting

### Category Management

- Create a category
- Get category by ID
- Get all categories
- Update a category
- Delete a category
- Prevent deletion of categories that are currently associated with products

### Validation & Exception Handling

- Request validation using Jakarta Bean Validation
- Product and category not-found handling
- Category-in-use handling
- Invalid pagination handling
- Invalid sorting field/direction handling
- Invalid price and quantity range handling
- Centralized exception handling using `@RestControllerAdvice`

## API Endpoints

### Product APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |
| GET | `/api/products/search` | Search products by name |
| GET | `/api/products/category/{categoryId}` | Get products by category |
| GET | `/api/products/filter` | Filter products |
| GET | `/api/products/price` | Get products by price range |

### Category APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/categories` | Create category |
| GET | `/api/categories` | Get all categories |
| GET | `/api/categories/{id}` | Get category by ID |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category |

## Pagination and Sorting

Product APIs support pagination and sorting.

Example:

```text
GET /api/products?page=0&size=5&sortBy=price&direction=desc