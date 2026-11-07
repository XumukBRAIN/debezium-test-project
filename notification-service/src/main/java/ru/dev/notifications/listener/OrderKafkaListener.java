package ru.dev.notifications.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.dev.notifications.dto.DebeziumEvent;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class OrderKafkaListener {

    private final JsonMapper jsonMapper;

    public OrderKafkaListener(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "orders.db.public.order")
    public void listen(String message) {
        log.info("Row json: {}", message);
        try {
            DebeziumEvent event = jsonMapper.readValue(message, DebeziumEvent.class);
            String op = event.getPayload().getOp();
            var after = event.getPayload().getAfter();
            if ("c".equals(op)) {
                log.info("Новый заказ: id={}, number={}, total={}, creation date={}", after.getId(), after.getNumber(), after.getTotalPrice(), after.getCreationDate());
            } else if ("u".equals(op)) {
                log.info("Заказ обновлён: id={}", after.getId());
            } else if ("d".equals(op)) {
                log.info("Заказ удалён: id={}", after.getId());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события из Kafka", e);
        }
    }
}
