package rs.raf.foodsystembackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rs.raf.foodsystembackend.dtos.CreateOrderDTO;
import rs.raf.foodsystembackend.dtos.OrderDTO;
import rs.raf.foodsystembackend.dtos.OrderSearchDTO;
import rs.raf.foodsystembackend.models.User;
import rs.raf.foodsystembackend.services.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('can_search_order')")
    public ResponseEntity<List<OrderDTO>> searchOrders(@RequestBody OrderSearchDTO searchDTO,
                                                      @AuthenticationPrincipal User user) {
        List<OrderDTO> orders = orderService.searchOrders(searchDTO, user);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('can_place_order')")
    public ResponseEntity<?> placeOrder(@RequestBody CreateOrderDTO createOrderDTO,
                                       @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.createOrder(createOrderDTO, user);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('can_cancel_order')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id,
                                        @AuthenticationPrincipal User user) {
        try {
            orderService.cancelOrder(id, user);
            return ResponseEntity.ok().body(Map.of("message", "Order canceled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/track")
    @PreAuthorize("hasAuthority('can_track_order')")
    public ResponseEntity<?> trackOrder(@PathVariable Long id,
                                       @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.trackOrder(id, user);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('can_schedule_order')")
    public ResponseEntity<?> scheduleOrder(@RequestBody CreateOrderDTO createOrderDTO,
                                          @AuthenticationPrincipal User user) {
        try {
            if (createOrderDTO.getScheduledFor() == null) {
                return ResponseEntity.badRequest().body("Scheduled time is required");
            }
            OrderDTO order = orderService.createOrder(createOrderDTO, user);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
