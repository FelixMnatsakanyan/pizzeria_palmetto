package am.trainings.palmetto.web;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import am.trainings.palmetto.customer.Order;
import am.trainings.palmetto.customer.service.OrderService;
import am.trainings.palmetto.pizza.Pizza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<String> findAll() {
        return orderService.findAll().stream().map(Object::toString).toList();
    }

    @GetMapping(path = "/{orderId}")
    public ResponseEntity<Order> findOrderById(@PathVariable("orderId") String orderId) {

        if (orderId == null || "".equals(orderId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        long orderIdLong;
        try {
            orderIdLong = Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            log.warn("Invalid order id '{}'", orderId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        Optional<Order> orderById = orderService.findOrderById(orderIdLong);

        return orderById.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody CreateOrderRequest request) {
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Map<Pizza, Integer> pizzas = Map.of();
        long orderId = orderService.orderPizza(request.username(), pizzas);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{orderId}")
                .buildAndExpand(orderId)
                .toUri();
        return ResponseEntity.created(location).body(orderId);
    }

    private record CreateOrderRequest(String username, Map<String, Integer> pizzas) {
    }
}
