package rs.raf.foodsystembackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.foodsystembackend.dtos.DishDTO;
import rs.raf.foodsystembackend.services.DishService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<DishDTO>> getAllDishes() {
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    @GetMapping("/available")
    public ResponseEntity<List<DishDTO>> getAvailableDishes() {
        return ResponseEntity.ok(dishService.getAvailableDishes());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<DishDTO>> getDishesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(dishService.getDishesByCategory(category));
    }
}
