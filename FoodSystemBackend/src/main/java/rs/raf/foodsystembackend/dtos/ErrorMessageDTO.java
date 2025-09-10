package rs.raf.foodsystembackend.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorMessageDTO {
    private Long id;
    private LocalDateTime timestamp;
    private Long orderId;
    private String operation;
    private String errorMessage;
    private Long userId;
    private String userName;
}
