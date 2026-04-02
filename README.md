# Order Service

Microservice responsible for managing customer orders and order items.

## Key responsibilities

- Creating new orders with multiple items and automatic total price calculation.
- Retrieving orders by ID or by user ID with pagination support.
- Updating order status (e.g., PENDING -> CONFIRMED -> CANCELLED -> COMPLETED).
- Modifying order items (adding, removing, or changing quantities) with automatic price recalculation.
- Deleting (cancelling) orders.
- Providing order data to other services via internal API endpoints.
- Filtering orders by date range and status for administrative reports.