package rs.raf.foodsystembackend.dtos;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> permissions;
}
