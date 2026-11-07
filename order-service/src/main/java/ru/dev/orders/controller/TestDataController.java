package ru.dev.orders.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dev.orders.dao.models.Order;
import ru.dev.orders.dao.repositories.OrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestDataController {

    private final OrderRepository orderRepository;

    public TestDataController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping("/generate-orders")
    public ResponseEntity<String> generateOrders() {
        int count = 100;
        for (int i = 0; i < count; i++) {
            String number = "ORD-" + String.format("%06d", i + 1);
            BigDecimal totalPrice = BigDecimal.valueOf(Math.random() * 100_000).setScale(2, RoundingMode.HALF_UP);
            LocalDate creationDate = LocalDate.now().minusDays((long) (Math.random() * 30));

            Order order = new Order()
                .setNumber(number)
                .setTotalPrice(totalPrice)
                .setCreationDate(creationDate);

            orderRepository.save(order);
        }
        log.info("Сгенерировано {} тестовых заказов", count);
        return ResponseEntity.ok("Сгенерировано " + count + " тестовых заказов");
    }
}
