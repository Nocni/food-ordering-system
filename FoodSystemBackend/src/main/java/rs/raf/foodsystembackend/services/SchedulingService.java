package rs.raf.foodsystembackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchedulingService {

    private final OrderService orderService;

    @Autowired
    public SchedulingService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 3000) // Check every minute
    @Transactional
    public void processScheduledOrders() {
        orderService.processScheduledOrders();
    }
}
