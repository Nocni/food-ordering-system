package rs.raf.foodsystembackend.dtos;

import lombok.Data;
import java.util.Set;

@Data
public class CreateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> permissions;
    private String password;
}
