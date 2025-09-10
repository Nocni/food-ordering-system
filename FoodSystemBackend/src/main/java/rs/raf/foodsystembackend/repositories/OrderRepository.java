package rs.raf.foodsystembackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.foodsystembackend.models.Order;
import rs.raf.foodsystembackend.models.OrderStatus;
import rs.raf.foodsystembackend.models.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCreatedByAndActiveTrue(User user);
    
    List<Order> findByCreatedBy(User user);
    
    List<Order> findByActiveTrue();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN :statuses AND o.active = true")
    long countByStatusInAndActiveTrue(@Param("statuses") List<OrderStatus> statuses);
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:statuses IS NULL OR o.status IN :statuses) AND " +
           "(:dateFrom IS NULL OR o.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR o.createdAt <= :dateTo) AND " +
           "(:user IS NULL OR o.createdBy = :user) AND " +
           "o.active = true")
    List<Order> searchOrders(@Param("statuses") List<OrderStatus> statuses,
                           @Param("dateFrom") LocalDateTime dateFrom,
                           @Param("dateTo") LocalDateTime dateTo,
                           @Param("user") User user);
    
    List<Order> findByScheduledForBeforeAndStatusAndActiveTrue(LocalDateTime time, OrderStatus status);
    
    List<Order> findByStatusAndActiveTrueAndIsProcessingFalse(OrderStatus status);
}
