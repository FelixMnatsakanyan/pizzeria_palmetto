package am.trainings.palmetto.web;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import am.trainings.palmetto.customer.Customer;
import am.trainings.palmetto.customer.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {
    private CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<Customer> registerCustomer(@RequestBody CustomerRequest request) {
        if(request == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Customer customer = new Customer(request.fullName, request.username);
        customerRepository.registerCustomer(customer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(customer.getUsername())
                .toUri();

        return ResponseEntity.created(location).body(customer);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Customer> findByUsername(@PathVariable("username") String username) {
        Optional<Customer> customer = customerRepository.findCustomerByUsername(username);
        return customer.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    private record CustomerRequest(String fullName, String username) {
    }


}
