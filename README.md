### Customer Order Service


Technologies used :-
Spring boot,Java,postgres db, h2 db, maven, docker, swagger, intellij IDE.

Project contains 2 microservices.
1. customer-order-service : to manage order.
		consist of below endpoints : 
			POST /api/v1/customer-orders -- Create order
			GET /api/v1/customer-orders/{id} -- Get order by ID
			GET /api/v1/customer-orders -- List all orders
			PATCH /api/v1/customer-orders/{id} -- Update order partially

2. product-catalog-service : to manage product catalog 
		consist of below endpoints
			POST /api/v1/product-offerings	Create product offering
			GET	/api/v1/product-offerings	Get all product offerings
			GET	/api/v1/product-offerings/{id}	Get product offering by ID

Steps for execution :
1.unzip zip file and navigate to folder containing order-management-system in terminal
2. run docker-compose up -- build. 
This should build and start both services.

3.Once services are up, swagger UI can be viewed on urls as follows :-
http://localhost:8080/swagger-ui/index.html --> Customer-order-service
http://localhost:8081/swagger-ui/index.html --> product-catalog-service

4.Order can be created using customer-order-service /api/v1/customer-orders api.
		(Please use any of the values from[ 'PO_1001','PO_1002','PO_1003','PO_1004','PO_1005'] as productOfferingId.  )

5.product can be created using product-catalog-service /api/v1/product-offerings api.

4.Tets cases can be executed as below :
	1.customer-order-service 
		cd customer-order-service
		mvn clean test
	2.product-catalog-service
		cd ../customer-order-service
		mvn clean test
			
Databse for customer-order-service can be accessed as below  :
JDBC URL: jdbc:postgresql://orderdb-postgres:5432/orderdb
Username: postgres
Password: postgres

Databse for product-catalog-service can be accessed as below  :
Connection details :
JDBC URL: jdbc:postgresql://productdb-postgres:5432/productdb
Username: postgres
Password: postgres


**What you built and what you cut** |
		- simple microservices-based order system with a Customer Order Service and a Product Catalog Service. 
		-The system supports core order operations like create, update, retrieve, and list orders, along with product management and validation through inter-service communication.
		-contians functionalities like order state management, idempotency to prevent duplicate orders, and basic error handling to ensure reliability
		
		Future improvemnts :
		-Kafka / event-driven architecture
		-Authentication & authorization
		-Inventory management system
		-Caching layer
**Decisions and tradeoffs** |
		state machine - Orders follow a controlled state machine:
						- DRAFT → SUBMITTED → CONFIRMED
						- DRAFT → CANCELLED
						- SUBMITTED → CANCELLED
					Invalid transitions are rejected via business validation.
		PATCH semantics -PATCH updates are partial and only apply to fields present in the request.
						Supported updates:
							-state (with validation via state machine)
							- category
							- orderItems
						Immutable or restricted states (e.g., CONFIRMED) cannot be modified.
		Idempotency - Idempotency is enforced using an `Idempotency-Key`.Stored using `OrderIdempotency` entity. Guarantees:
							Same key + same request → returns same result (no duplicate order creation)
							Same key + different request → rejected (conflict handling)
 							Prevents duplicate processing at service level
		Product Validation (External Dependency) -Before order creation, product IDs are validated using an external Product Catalog service.
					Validation flow:
					- Async validation using CompletableFuture
					- Timeout protection (fail fast strategy)
					- If product service is unavailable → request is rejected
					This ensures only valid products are persisted in orders.
		Failure Handling Strategy
				- All validation errors return **400 Bad Request**
				- Missing resources return **404 Not Found**
				- Idempotency conflicts return **409 Conflict**
				- Unexpected errors are handled via global exception handler
				A consistent error response format is maintained across all APIs.
		Exception Handling -A global exception handler ensures:
				- No stack traces exposed to clients
				- Consistent error structure
				- Proper HTTP status mapping