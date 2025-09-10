package rs.raf.foodsystembackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rs.raf.foodsystembackend.dtos.CreateUserDTO;
import rs.raf.foodsystembackend.dtos.UserDTO;
import rs.raf.foodsystembackend.models.User;
import rs.raf.foodsystembackend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserDTO createUser(CreateUserDTO user) {
        // Validate required fields
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty() ||
            user.getLastName() == null || user.getLastName().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("All fields (firstName, lastName, email, password) are required");
        }
        
        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPermissions(user.getPermissions() != null ? user.getPermissions() : new HashSet<>());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(newUser);
        return mapToDTO(savedUser);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id).map(this::mapToDTO);
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(UserDTO updateUser) {
        User user = userRepository.getUserById(updateUser.getId());
        user.setEmail(updateUser.getEmail());
        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPermissions(updateUser.getPermissions());
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPermissions(user.getPermissions());
        return userDTO;
    }
}
