package com.example.marketorder.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountId;
    private String symbol;

    @Enumerated(EnumType.STRING)
    private Side side;

    private int quantity;
    private String status;
    private LocalDateTime createdAt;

    // Getters and setters

    public enum Side {
        BUY, SELL
    }
}
