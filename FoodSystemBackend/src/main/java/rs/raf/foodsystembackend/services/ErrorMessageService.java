package rs.raf.foodsystembackend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.raf.foodsystembackend.dtos.ErrorMessageDTO;
import rs.raf.foodsystembackend.models.ErrorMessage;
import rs.raf.foodsystembackend.models.User;
import rs.raf.foodsystembackend.repositories.ErrorMessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErrorMessageService {

    private final ErrorMessageRepository errorMessageRepository;

    public ErrorMessageService(ErrorMessageRepository errorMessageRepository) {
        this.errorMessageRepository = errorMessageRepository;
    }

    public Page<ErrorMessageDTO> getErrorsForUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        
        // Check if user has admin permissions
        boolean isAdmin = user.getPermissions().contains("can_read_users");
        
        Page<ErrorMessage> errorMessages;
        if (isAdmin) {
            // Admin can see all errors
            errorMessages = errorMessageRepository.findAll(pageable);
        } else {
            // Regular users can only see their own errors
            errorMessages = errorMessageRepository.findByUser(user, pageable);
        }
        
        return errorMessages.map(this::mapToDTO);
    }

    public List<ErrorMessageDTO> getAllErrorsForUser(User user) {
        // Check if user has admin permissions
        boolean isAdmin = user.getPermissions().contains("can_read_users");
        
        List<ErrorMessage> errorMessages;
        if (isAdmin) {
            // Admin can see all errors
            errorMessages = errorMessageRepository.findAll(Sort.by("timestamp").descending());
        } else {
            // Regular users can only see their own errors
            errorMessages = errorMessageRepository.findByUserOrderByTimestampDesc(user);
        }
        
        return errorMessages.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ErrorMessageDTO mapToDTO(ErrorMessage errorMessage) {
        ErrorMessageDTO dto = new ErrorMessageDTO();
        dto.setId(errorMessage.getId());
        dto.setTimestamp(errorMessage.getTimestamp());
        dto.setOrderId(errorMessage.getOrderId());
        dto.setOperation(errorMessage.getOperation());
        dto.setErrorMessage(errorMessage.getErrorMessage());
        
        if (errorMessage.getUser() != null) {
            dto.setUserId(errorMessage.getUser().getId());
            dto.setUserName(errorMessage.getUser().getFirstName() + " " + errorMessage.getUser().getLastName());
        }
        
        return dto;
    }
}
