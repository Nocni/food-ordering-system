package rs.raf.foodsystembackend.dtos;

import lombok.Data;
import rs.raf.foodsystembackend.models.OrderStatus;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderSearchDTO {
    private List<OrderStatus> status;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Long userId;
}
