# Food Ordering System

A full-stack food ordering application built with Spring Boot (backend) and Angular (frontend). This system allows users to place immediate or scheduled food orders, track order status in real-time, and manage the complete order lifecycle.

## 🚀 Features

### User Management
- **User Registration & Authentication** with JWT tokens
- **Role-based Access Control** with granular permissions
- **Admin Panel** for user management
- **Profile Management** with permission assignment

### Order Management
- **Immediate Orders** - Start processing right away
- **Scheduled Orders** - Place orders for future delivery
- **Real-time Order Tracking** with status updates:
  - ORDERED → PREPARING → IN_DELIVERY → DELIVERED
- **Order Cancellation** (only while in ORDERED status)
- **Multiple Dish Quantities** support
- **Concurrent Order Limits** to prevent system overload

### Dish Management
- **Dish Catalog** with categories and pricing
- **Availability Management**
- **Dynamic Menu Updates**

### System Features
- **Automatic Order Processing** with configurable timing
- **Error Logging & Monitoring**
- **Input Validation & Security**
- **Responsive Design** for multiple devices

## 🏗️ Architecture

### Backend (Spring Boot)
- **RESTful API** with comprehensive endpoints
- **JWT Authentication & Authorization**
- **JPA/Hibernate** for database management
- **H2 In-Memory Database** for development
- **Async Processing** for order workflow
- **Scheduled Tasks** for order management
- **CORS Configuration** for frontend integration

### Frontend (Angular)
- **Modern Angular** application with TypeScript
- **JWT Interceptor** for automatic authentication
- **Real-time Updates** with polling
- **Responsive UI** with CSS styling
- **Permission-based Components** for role management
- **Form Validation** and error handling

## 📋 Prerequisites

- **Java 17+**
- **Node.js 18+**
- **Angular CLI**
- **Maven 3.6+**
- **Git**

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/food-ordering-system.git
cd food-ordering-system
```

### 2. Backend Setup
```bash
cd FoodSystemBackend

# Compile the project
mvn clean compile

# Run the backend server
mvn spring-boot:run
```
The backend will start on `http://localhost:8080`

### 3. Frontend Setup
```bash
cd FoodSystemFrontend

# Install dependencies
npm install

# Start the development server
ng serve
```
The frontend will be available at `http://localhost:4200`

## 🎯 Usage

### Default Users
The system creates default users on startup:

**Admin User:**
- Email: `admin@example.com`
- Password: `admin123`
- Permissions: All system permissions

**Regular User:**
- Email: `user@example.com`
- Password: `user123`
- Permissions: Basic user operations

### Key Workflows

1. **Login** → Access the system with provided credentials
2. **Browse Menu** → View available dishes and select items
3. **Place Order** → Choose immediate delivery or schedule for later
4. **Track Order** → Monitor real-time status updates
5. **Cancel Order** → Cancel orders while still in ORDERED status
6. **Admin Management** → Manage users and view all orders (admin only)

## 🔧 Configuration

### Backend Configuration
Key settings in `application.properties`:
- Database configuration (H2 in-memory)
- JWT token settings
- CORS configuration
- Logging levels

### Frontend Configuration
Environment settings for API endpoints and authentication.

## 🏃‍♂️ Order Processing Flow

1. **Order Placement** → Order created with ORDERED status
2. **Validation** → Check dish availability and user permissions
3. **Processing Start** → Automatic transition to PREPARING (5-8 seconds)
4. **Preparation** → Order moves to IN_DELIVERY (8-12 seconds)
5. **Delivery** → Final transition to DELIVERED (10-15 seconds)

*Note: Timing is reduced for demonstration purposes*

## 🛡️ Security Features

- **JWT-based Authentication**
- **Password Encryption**
- **Role-based Authorization**
- **Input Validation**
- **CORS Protection**
- **Error Message Sanitization**

## 📊 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Orders
- `POST /api/orders` - Place new order
- `POST /api/orders/search` - Search orders
- `PUT /api/orders/{id}/cancel` - Cancel order
- `GET /api/orders/{id}/track` - Track order

### Users (Admin)
- `GET /api/users` - List all users
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Dishes
- `GET /api/dishes` - Get available dishes

## 🧪 Testing

### Backend Testing
```bash
cd FoodSystemBackend
mvn test
```

### Frontend Testing
```bash
cd FoodSystemFrontend
ng test
```

## 📁 Project Structure

```
food-ordering-system/
├── FoodSystemBackend/          # Spring Boot backend
│   ├── src/main/java/
│   │   └── rs/raf/foodsystembackend/
│   │       ├── controllers/    # REST controllers
│   │       ├── services/       # Business logic
│   │       ├── models/         # Entity models
│   │       ├── repositories/   # Data access
│   │       ├── dtos/          # Data transfer objects
│   │       └── security/       # Security configuration
│   └── pom.xml                # Maven dependencies
├── FoodSystemFrontend/         # Angular frontend
│   ├── src/app/
│   │   ├── components/        # UI components
│   │   ├── services/          # API services
│   │   ├── models/            # TypeScript models
│   │   ├── interceptors/      # HTTP interceptors
│   │   └── security/          # Authentication guards
│   └── package.json           # npm dependencies
└── README.md                  # This file
```

## 🔧 Technical Highlights

- **Async Order Processing** with status validation
- **Cancellation Support** with proper cleanup
- **Multiple Quantity Handling** for dishes
- **Scheduled Order Management** with automated processing
- **Real-time UI Updates** with polling mechanism
- **Comprehensive Error Handling** and logging
- **JWT Security** with role-based access control

## 🚀 Future Enhancements

- WebSocket integration for real-time updates
- Payment gateway integration
- Email/SMS notifications
- Order history and analytics
- Restaurant management features
- Mobile application

## 👥 Contributing

This is a college project. For educational purposes, feel free to:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📝 License

This project is created for educational purposes as part of a college assignment.

## 🙋‍♂️ Support

For questions or issues related to this project, please create an issue in the GitHub repository.

---

**Built with ❤️ for educational purposes**
