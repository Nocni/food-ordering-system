package rs.raf.foodsystembackend.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import rs.raf.foodsystembackend.models.User;
import rs.raf.foodsystembackend.models.Dish;
import rs.raf.foodsystembackend.repositories.UserRepository;
import rs.raf.foodsystembackend.repositories.DishRepository;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, DishRepository dishRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initializeUsers();
        initializeDishes();
    }
    
    private void initializeUsers() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setPermissions(Set.of(
                    "can_create_users",
                    "can_read_users",
                    "can_update_users",
                    "can_delete_users",
                    "can_search_order",
                    "can_place_order",
                    "can_cancel_order",
                    "can_track_order",
                    "can_schedule_order"
            ));

            userRepository.save(adminUser);
            System.out.println("Admin user created successfully!");
        }
        
        if (!userRepository.existsByEmail("user@example.com")) {
            User regularUser = new User();
            regularUser.setFirstName("Regular");
            regularUser.setLastName("User");
            regularUser.setEmail("user@example.com");
            regularUser.setPassword(passwordEncoder.encode("user123"));
            regularUser.setPermissions(Set.of(
                    "can_search_order",
                    "can_place_order",
                    "can_track_order",
                    "can_schedule_order"
            ));

            userRepository.save(regularUser);
            System.out.println("Regular user created successfully!");
        }
    }
    
    private void initializeDishes() {
        if (dishRepository.count() == 0) {
            createDish("Margherita Pizza", "Classic pizza with tomato sauce, mozzarella and basil", 
                      new BigDecimal("12.99"), "Pizza");
            createDish("Pepperoni Pizza", "Pizza with pepperoni, mozzarella and tomato sauce", 
                      new BigDecimal("15.99"), "Pizza");
            createDish("Caesar Salad", "Fresh romaine lettuce with Caesar dressing and croutons", 
                      new BigDecimal("8.99"), "Salad");
            createDish("Grilled Chicken", "Tender grilled chicken breast with herbs", 
                      new BigDecimal("18.99"), "Main Course");
            createDish("Beef Burger", "Juicy beef burger with lettuce, tomato and cheese", 
                      new BigDecimal("14.99"), "Burger");
            createDish("Chocolate Cake", "Rich chocolate cake with chocolate frosting", 
                      new BigDecimal("6.99"), "Dessert");
            
            System.out.println("Sample dishes created successfully!");
        }
    }
    
    private void createDish(String name, String description, BigDecimal price, String category) {
        Dish dish = new Dish();
        dish.setName(name);
        dish.setDescription(description);
        dish.setPrice(price);
        dish.setCategory(category);
        dish.setAvailable(true);
        dishRepository.save(dish);
    }
}
