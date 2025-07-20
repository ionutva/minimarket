package com.example.marketorder.repository;

import com.example.marketorder.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAccountId(String accountId);
}
