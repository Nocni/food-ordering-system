package rs.raf.foodsystembackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.foodsystembackend.models.ErrorMessage;
import rs.raf.foodsystembackend.models.User;

import java.util.List;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {
    Page<ErrorMessage> findByUser(User user, Pageable pageable);
    List<ErrorMessage> findByUserOrderByTimestampDesc(User user);
}
