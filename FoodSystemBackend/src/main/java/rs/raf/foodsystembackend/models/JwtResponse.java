package rs.raf.foodsystembackend.models;

import lombok.Data;

@Data
public class JwtResponse {

    private String jwt;
    private User user;

    public JwtResponse(String jwt, User user) {
        this.jwt = jwt;
        this.user = user;
    }
}
