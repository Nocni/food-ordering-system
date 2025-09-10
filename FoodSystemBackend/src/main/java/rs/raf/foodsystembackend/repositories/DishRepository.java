package rs.raf.foodsystembackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.foodsystembackend.models.Dish;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByAvailableTrue();
    List<Dish> findByCategory(String category);
}
