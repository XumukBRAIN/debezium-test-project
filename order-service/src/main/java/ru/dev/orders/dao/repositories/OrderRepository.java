package ru.dev.orders.dao.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.dev.orders.dao.models.Order;

public interface OrderRepository extends ListCrudRepository<Order, Long> {
}
