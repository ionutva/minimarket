import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@SpringBootApplication
@RestController
public class PriceFeedMockApplication {
    public static void main(String[] args) {
        SpringApplication.run(PriceFeedMockApplication.class, args);
    }

    @GetMapping("/price")
    public Map<String, Object> getPrice(@RequestParam String symbol) {
        return Map.of("symbol", symbol, "price", new BigDecimal("210.550000"));
    }
}
