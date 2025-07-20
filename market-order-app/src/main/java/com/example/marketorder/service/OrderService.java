package com.example.marketorder.service;

import com.example.marketorder.model.Order;
import com.example.marketorder.model.Execution;
import com.example.marketorder.repository.OrderRepository;
import com.example.marketorder.repository.ExecutionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ExecutionRepository executionRepository;
    private final RestTemplate restTemplate;

    @Value("${price.feed.url:http://price-feed:8081/price?symbol=}")
    private String priceFeedUrl;

    public OrderService(OrderRepository orderRepository, ExecutionRepository executionRepository) {
        this.orderRepository = orderRepository;
        this.executionRepository = executionRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public BigDecimal createOrder(String accountId, String symbol, String side, int quantity) {
        if (quantity <= 0 || (!side.equalsIgnoreCase("BUY") && !side.equalsIgnoreCase("SELL"))) {
            throw new IllegalArgumentException("Invalid order parameters");
        }

        BigDecimal price = fetchPriceWithRetry(symbol);

        Order order = new Order();
        order.setAccountId(accountId);
        order.setSymbol(symbol);
        order.setSide(Order.Side.valueOf(side.toUpperCase()));
        order.setQuantity(quantity);
        order.setStatus("EXECUTED");
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        Execution execution = new Execution();
        execution.setOrderId(order.getId());
        execution.setPrice(price);
        execution.setExecutedAt(LocalDateTime.now());
        executionRepository.save(execution);

        return price;
    }

    private BigDecimal fetchPriceWithRetry(String symbol) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(priceFeedUrl + symbol, Map.class);
                Map body = response.getBody();
                if (body != null && body.containsKey("price")) {
                    return new BigDecimal(body.get("price").toString());
                } else {
                    throw new IllegalArgumentException("Invalid price response");
                }
            } catch (HttpServerErrorException e) {
                attempts++;
                if (attempts >= 2) {
                    throw new RuntimeException("Failed to fetch price after retry", e);
                }
            } catch (RestClientException e) {
                throw new RuntimeException("Price feed unreachable", e);
            }
        }
        throw new RuntimeException("Failed to fetch price");
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByAccountId(String accountId) {
        return orderRepository.findByAccountId(accountId);
    }
}
