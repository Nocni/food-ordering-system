package rs.raf.foodsystembackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.foodsystembackend.dtos.*;
import rs.raf.foodsystembackend.models.*;
import rs.raf.foodsystembackend.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final ErrorMessageRepository errorMessageRepository;
    private final Random random = new Random();
    
    private static final int MAX_CONCURRENT_ORDERS = 3;
    private static final int MAX_DISHES_PER_ORDER = 50; // Add reasonable limit per order

    @Autowired
    public OrderService(OrderRepository orderRepository, DishRepository dishRepository, 
                       UserRepository userRepository, ErrorMessageRepository errorMessageRepository) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.errorMessageRepository = errorMessageRepository;
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderDTO createOrderDTO, User user) throws Exception {
        // Validate order has dishes
        if (createOrderDTO.getDishIds() == null || createOrderDTO.getDishIds().isEmpty()) {
            String errorMsg = "Order must contain at least one dish";
            logError("PLACE_ORDER", null, errorMsg, user);
            throw new Exception(errorMsg);
        }
        
        // Validate maximum dishes per order
        if (createOrderDTO.getDishIds().size() > MAX_DISHES_PER_ORDER) {
            String errorMsg = "Order exceeds maximum allowed dishes (" + MAX_DISHES_PER_ORDER + ")";
            logError("PLACE_ORDER", null, errorMsg, user);
            throw new Exception(errorMsg);
        }
        
        // Check concurrent orders limit
        long activeOrders = orderRepository.countByStatusInAndActiveTrue(
            List.of(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY)
        );
        
        if (activeOrders >= MAX_CONCURRENT_ORDERS) {
            String errorMsg = "Maximum number of concurrent orders reached";
            logError("PLACE_ORDER", null, errorMsg, user);
            throw new Exception(errorMsg);
        }
        
        // Validate dishes - check if all unique dish IDs exist
        List<Long> uniqueDishIds = createOrderDTO.getDishIds().stream().distinct().collect(Collectors.toList());
        List<Dish> dishes = dishRepository.findAllById(uniqueDishIds);
        if (dishes.size() != uniqueDishIds.size()) {
            String errorMsg = "Some dishes not found";
            logError("PLACE_ORDER", null, errorMsg, user);
            throw new Exception(errorMsg);
        }
        
        // Get all dishes for the order (including duplicates for quantities)
        List<Dish> orderDishes = createOrderDTO.getDishIds().stream()
            .map(dishId -> dishes.stream()
                .filter(dish -> dish.getId().equals(dishId))
                .findFirst()
                .orElse(null))
            .collect(Collectors.toList());
        
        // Create order
        Order order = new Order();
        order.setCreatedBy(user);
        order.setItems(orderDishes);
        order.setStatus(OrderStatus.ORDERED);
        order.setActive(true);
        order.setScheduledFor(createOrderDTO.getScheduledFor());
        
        if (createOrderDTO.getScheduledFor() == null || 
            createOrderDTO.getScheduledFor().isBefore(LocalDateTime.now().plusMinutes(1))) {
            order.setIsProcessing(false); // Initially not processing
            order = orderRepository.save(order);
            System.out.println("Order created with ID: " + order.getId() + " - will start processing after transaction commit");
            
            // Schedule async processing to start after transaction commits
            final Long orderIdForAsync = order.getId();
            CompletableFuture.runAsync(() -> {
                try {
                    // Small delay to ensure transaction is committed
                    Thread.sleep(500);
                    processOrderAsync(orderIdForAsync);
                } catch (Exception e) {
                    System.err.println("Error starting async order processing: " + e.getMessage());
                }
            });
        } else {
            // SCHEDULED ORDER - save but don't process yet
            order.setIsProcessing(false);
            order = orderRepository.save(order);
            System.out.println("Order created with ID: " + order.getId() + " - scheduled for: " + order.getScheduledFor());
        }
        
        return mapToDTO(order);
    }

    // Non-async method to handle the actual processing logic
    private void processOrderAsync(Long orderId) {
        try {
            System.out.println("Starting async processing for order " + orderId);
            
            // Mark as processing
            markOrderAsProcessing(orderId, true);
            
            // ORDERED -> PREPARING (5+ seconds with deviation - shorter for testing)
            System.out.println("Order " + orderId + " is ORDERED, transitioning to PREPARING in 5-8 seconds...");
            Thread.sleep((5 + random.nextInt(4)) * 1000);
            
            // Check if order is still active and in ORDERED status before transitioning
            if (!isOrderValidForProcessing(orderId, OrderStatus.ORDERED)) {
                System.out.println("Order " + orderId + " is no longer in ORDERED status, stopping processing");
                markOrderAsProcessing(orderId, false);
                return;
            }
            
            updateOrderStatus(orderId, OrderStatus.PREPARING);
            System.out.println("Order " + orderId + " is now PREPARING, transitioning to IN_DELIVERY in 8-12 seconds...");
            
            // PREPARING -> IN_DELIVERY (8+ seconds with deviation)
            Thread.sleep((8 + random.nextInt(5)) * 1000);
            
            // Check if order is still active and in PREPARING status before transitioning
            if (!isOrderValidForProcessing(orderId, OrderStatus.PREPARING)) {
                System.out.println("Order " + orderId + " is no longer in PREPARING status, stopping processing");
                markOrderAsProcessing(orderId, false);
                return;
            }
            
            updateOrderStatus(orderId, OrderStatus.IN_DELIVERY);
            System.out.println("Order " + orderId + " is now IN_DELIVERY, transitioning to DELIVERED in 10-15 seconds...");
            
            // IN_DELIVERY -> DELIVERED (10+ seconds with deviation)
            Thread.sleep((10 + random.nextInt(6)) * 1000);
            
            // Check if order is still active and in IN_DELIVERY status before transitioning
            if (!isOrderValidForProcessing(orderId, OrderStatus.IN_DELIVERY)) {
                System.out.println("Order " + orderId + " is no longer in IN_DELIVERY status, stopping processing");
                markOrderAsProcessing(orderId, false);
                return;
            }
            
            updateOrderStatus(orderId, OrderStatus.DELIVERED);
            System.out.println("Order " + orderId + " is now DELIVERED!");
            
            // Mark as no longer processing
            markOrderAsProcessing(orderId, false);
            
        } catch (Exception e) {
            System.err.println("Error processing order " + orderId + ": " + e.getMessage());
            try {
                markOrderAsProcessing(orderId, false);
            } catch (Exception markError) {
                System.err.println("Error marking order as not processing: " + markError.getMessage());
            }
        }
    }

    private boolean isOrderValidForProcessing(Long orderId, OrderStatus expectedStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            return order.getActive() && order.getStatus() == expectedStatus;
        }
        return false;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getActive()) {
                order.setStatus(newStatus);
                order.setStatusUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                System.out.println("Updated order " + orderId + " status to: " + newStatus);
            }
        }
    }

    @Transactional
    public void markOrderAsProcessing(Long orderId, boolean isProcessing) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setIsProcessing(isProcessing);
            orderRepository.save(order);
            System.out.println("Marked order " + orderId + " as processing: " + isProcessing);
        }
    }

    public List<OrderDTO> searchOrders(OrderSearchDTO searchDTO, User user) {
        List<OrderStatus> statuses = searchDTO.getStatus();
        LocalDateTime dateFrom = searchDTO.getDateFrom() != null ? 
            searchDTO.getDateFrom().atStartOfDay() : null;
        LocalDateTime dateTo = searchDTO.getDateTo() != null ? 
            searchDTO.getDateTo().atTime(23, 59, 59) : null;
        
        User targetUser = null;
        
        // Check if user has admin permissions
        boolean isAdmin = user.getPermissions().contains("can_read_users");
        
        if (isAdmin && searchDTO.getUserId() != null) {
            targetUser = userRepository.findById(searchDTO.getUserId()).orElse(null);
        } else if (!isAdmin) {
            targetUser = user; // Regular users can only see their own orders
        }
        
        List<Order> orders = orderRepository.searchOrders(statuses, dateFrom, dateTo, targetUser);
        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrder(Long orderId, User user) throws Exception {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new Exception("Order not found"));
        
        // Check if user owns the order or is admin
        if (!order.getCreatedBy().getId().equals(user.getId()) && 
            !user.getPermissions().contains("can_read_users")) {
            String errorMsg = "Access denied";
            logError("CANCEL_ORDER", orderId, errorMsg, user);
            throw new Exception(errorMsg);
        }
        
        if (order.getStatus() != OrderStatus.ORDERED) {
            String errorMsg = "Can only cancel orders in ORDERED status";
            logError("CANCEL_ORDER", orderId, errorMsg, user);
            throw new Exception(errorMsg);
        }
        
        order.setStatus(OrderStatus.CANCELED);
        order.setStatusUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    public OrderDTO trackOrder(Long orderId, User user) throws Exception {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new Exception("Order not found"));
        
        // Check if user owns the order or is admin
        if (!order.getCreatedBy().getId().equals(user.getId()) && 
            !user.getPermissions().contains("can_read_users")) {
            String errorMsg = "Access denied";
            logError("TRACK_ORDER", orderId, errorMsg, user);
            throw new Exception(errorMsg);
        }
        
        return mapToDTO(order);
    }

    public void processScheduledOrders() {
        List<Order> scheduledOrders = orderRepository.findByScheduledForBeforeAndStatusAndActiveTrue(
            LocalDateTime.now(), OrderStatus.ORDERED);
        
        System.out.println("Found " + scheduledOrders.size() + " scheduled orders to process at " + LocalDateTime.now());
        
        for (Order order : scheduledOrders) {
            try {
                // Check concurrent orders limit
                long activeOrders = orderRepository.countByStatusInAndActiveTrue(
                    List.of(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY)
                );
                
                if (activeOrders >= MAX_CONCURRENT_ORDERS) {
                    String errorMsg = "Maximum number of concurrent orders reached for scheduled order";
                    logError("SCHEDULE_ORDER", order.getId(), errorMsg, order.getCreatedBy());
                    System.out.println("Skipping scheduled order " + order.getId() + " - concurrent limit reached");
                    continue;
                }
                
                // Use the same processing method as immediate orders (with status checks)
                System.out.println("Starting scheduled order processing for order: " + order.getId());
                final Long orderIdForAsync = order.getId();
                CompletableFuture.runAsync(() -> {
                    try {
                        // Small delay to ensure any transaction is committed
                        Thread.sleep(500);
                        processOrderAsync(orderIdForAsync);
                    } catch (Exception e) {
                        System.err.println("Error starting scheduled order processing: " + e.getMessage());
                    }
                });
                
            } catch (Exception e) {
                logError("SCHEDULE_ORDER", order.getId(), 
                        "Failed to process scheduled order: " + e.getMessage(), 
                        order.getCreatedBy());
                System.err.println("Error processing scheduled order " + order.getId() + ": " + e.getMessage());
            }
        }
    }

    private void logError(String operation, Long orderId, String errorMessage, User user) {
        ErrorMessage error = new ErrorMessage();
        error.setOrderId(orderId);
        error.setOperation(operation);
        error.setErrorMessage(errorMessage);
        error.setUser(user);
        error.setTimestamp(LocalDateTime.now());
        errorMessageRepository.save(error);
    }

    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setCreatedById(order.getCreatedBy().getId());
        dto.setCreatedByName(order.getCreatedBy().getFirstName() + " " + order.getCreatedBy().getLastName());
        dto.setActive(order.getActive());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setScheduledFor(order.getScheduledFor());
        dto.setStatusUpdatedAt(order.getStatusUpdatedAt());
        
        List<DishDTO> dishDTOs = order.getItems().stream()
            .map(this::mapDishToDTO)
            .collect(Collectors.toList());
        dto.setItems(dishDTOs);
        
        return dto;
    }

    private DishDTO mapDishToDTO(Dish dish) {
        DishDTO dto = new DishDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setDescription(dish.getDescription());
        dto.setPrice(dish.getPrice());
        dto.setCategory(dish.getCategory());
        dto.setAvailable(dish.getAvailable());
        return dto;
    }
}
