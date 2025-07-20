package com.example.marketorder.controller;

import com.example.marketorder.model.Order;
import com.example.marketorder.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // DTO for request body
    public static class OrderRequest {
        @NotBlank
        private String accountId;

        @NotBlank
        private String symbol;

        @NotBlank
        private String side;

        @NotNull
        @Min(1)
        private Integer quantity;

        // Getters and setters
        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }

        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }

        public String getSide() { return side; }
        public void setSide(String side) { this.side = side; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    @Operation(summary = "Create a new market order")
    @PostMapping
    @RateLimiter(name = "orderLimiter")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest request) {
        try {
            BigDecimal executedPrice = orderService.createOrder(
                request.getAccountId(),
                request.getSymbol(),
                request.getSide(),
                request.getQuantity()
            );
            return ResponseEntity.ok().body(
                String.format("Order executed at price: %s", executedPrice.toPlainString())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(503).body("Price feed error or internal issue");
        }
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all orders for an account")
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByAccount(@RequestParam String accountId) {
        return ResponseEntity.ok(orderService.getOrdersByAccountId(accountId));
    }
}
