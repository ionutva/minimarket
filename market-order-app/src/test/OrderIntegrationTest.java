package com.example.order;

import com.example.order.dto.OrderRequest;
import com.example.order.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateOrderIntegration() {
        OrderRequest request = new OrderRequest();
        request.setAccountId("acc-" + UUID.randomUUID());
        request.setSymbol("AAPL");
        request.setSide("BUY");
        request.setQuantity(10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Order> response = restTemplate.exchange(
                "http://localhost:" + port + "/orders",
                HttpMethod.POST,
                entity,
                Order.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSymbol()).isEqualTo("AAPL");
        assertThat(response.getBody().getQuantity()).isEqualTo(10);
        assertThat(response.getBody().getPrice()).isGreaterThan(BigDecimal.ZERO);
    }
}
