package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.jwt.JWTUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return this.customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) {
        return this.customerService.getCustomerById(customerId);
    }

    @PostMapping
    public ResponseEntity<?> saveCustomer(@RequestBody CustomerRegistrationRequest request) {
        this.customerService.saveCustomer(request);
        String token = this.jwtUtil.issueToken(request.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .build();
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable(name = "id") Long id) {
        this.customerService.deleteCustomerById(id);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable(name = "id") Long id,
                                   @RequestBody CustomerUpdateRequest request) {
        return this.customerService.updateCustomer(id, request);
    }
}
