package rs.raf.foodsystembackend.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Boolean available;
}
