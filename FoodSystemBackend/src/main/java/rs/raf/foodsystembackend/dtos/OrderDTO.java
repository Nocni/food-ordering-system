package rs.raf.foodsystembackend.dtos;

import lombok.Data;
import rs.raf.foodsystembackend.models.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private OrderStatus status;
    private Long createdById;
    private String createdByName;
    private Boolean active;
    private List<DishDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledFor;
    private LocalDateTime statusUpdatedAt;
}
