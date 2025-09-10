package rs.raf.foodsystembackend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rs.raf.foodsystembackend.dtos.ErrorMessageDTO;
import rs.raf.foodsystembackend.models.User;
import rs.raf.foodsystembackend.services.ErrorMessageService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/errors")
public class ErrorMessageController {

    private final ErrorMessageService errorMessageService;

    public ErrorMessageController(ErrorMessageService errorMessageService) {
        this.errorMessageService = errorMessageService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('can_search_order')")
    public ResponseEntity<Page<ErrorMessageDTO>> getErrors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Page<ErrorMessageDTO> errors = errorMessageService.getErrorsForUser(user, page, size);
        return ResponseEntity.ok(errors);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('can_search_order')")
    public ResponseEntity<List<ErrorMessageDTO>> getAllErrors(@AuthenticationPrincipal User user) {
        List<ErrorMessageDTO> errors = errorMessageService.getAllErrorsForUser(user);
        return ResponseEntity.ok(errors);
    }
}
