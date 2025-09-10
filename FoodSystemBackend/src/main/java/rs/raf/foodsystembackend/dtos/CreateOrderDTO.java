package rs.raf.foodsystembackend.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOrderDTO {
    private List<Long> dishIds;
    private LocalDateTime scheduledFor;
}
