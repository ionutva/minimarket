
package com.example.order;

import com.example.order.model.Order;
import com.example.order.model.OrderSide;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.ExecutionRepository;
import com.example.order.service.OrderService;
import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.client.PriceFeedClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private ExecutionRepository executionRepository;
    private PriceFeedClient priceFeedClient;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        executionRepository = mock(ExecutionRepository.class);
        priceFeedClient = mock(PriceFeedClient.class);
        orderService = new OrderService(orderRepository, executionRepository, priceFeedClient);
    }

    @Test
    void testPlaceOrderSuccess() {
        OrderRequest request = new OrderRequest("acc-123", "AAPL", OrderSide.BUY, 10);
        BigDecimal price = new BigDecimal("210.55");

        when(priceFeedClient.getPrice("AAPL")).thenReturn(price);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        OrderResponse response = orderService.placeOrder(request);

        assertNotNull(response);
        assertEquals("AAPL", response.getSymbol());
        assertEquals(price, response.getExecutedPrice());
        verify(orderRepository, times(1)).save(any());
        verify(executionRepository, times(1)).save(any());
    }

    @Test
    void testInvalidQuantityThrowsException() {
        OrderRequest request = new OrderRequest("acc-123", "AAPL", OrderSide.BUY, 0);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(request);
        });

        assertEquals("Quantity must be greater than 0", thrown.getMessage());
    }

    @Test
    void testInvalidSideThrowsException() {
        // Assuming invalid sides are handled elsewhere, so no test here
        // Alternatively, use null and expect a NullPointerException or custom validation
        OrderRequest request = new OrderRequest("acc-123", "AAPL", null, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(request);
        });
    }
}
