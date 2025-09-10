package rs.raf.foodsystembackend.models;

import lombok.Data;

@Data
public class JwtRequest {

    private String email;
    private String password;

}
