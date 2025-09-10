package rs.raf.foodsystembackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.raf.foodsystembackend.dtos.DishDTO;
import rs.raf.foodsystembackend.models.Dish;
import rs.raf.foodsystembackend.repositories.DishRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishService {

    private final DishRepository dishRepository;

    @Autowired
    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<DishDTO> getAllDishes() {
        return dishRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DishDTO> getAvailableDishes() {
        return dishRepository.findByAvailableTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DishDTO> getDishesByCategory(String category) {
        return dishRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private DishDTO mapToDTO(Dish dish) {
        DishDTO dto = new DishDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setDescription(dish.getDescription());
        dto.setPrice(dish.getPrice());
        dto.setCategory(dish.getCategory());
        dto.setAvailable(dish.getAvailable());
        return dto;
    }
}
