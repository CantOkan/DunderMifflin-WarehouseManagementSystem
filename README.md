# DunderMifflin-WarehouseManagementSystem
The Warehouse Management System which is designed to meet the all need of the best paper company's warehouse
#### this project is build up as a simple Warehouse Management systems
##### Basically In version V1,Orders will hold in mongoDB, clients and employees will be in postgre

V1 Task:
- [X] Impl PostgreSQL
  - [X] Employee,Client,Address
  - [ ] Roles
- [X] Impl MongoDB
  - [X] Orders
- [ ] Impl RabbitMQ


V1 Business:
- OrderService
  - [ ] Update(Shipping etc.), Delete,
  - [ ] Hold geolocation(for googleMaps) of the address
  - [ ] GroupByLocation etc.
- ClientService
  - [ ] Update, Delete
- EmployeeService
  - [ ] Create,Update,Delete,Read
  - [ ] Roles declaration
  - [ ] Impl. Orders-Client relation






