package mk.ukim.finki.application.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return this.customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable(name = "id") Integer customerId) {
        return this.customerService.getCustomerById(customerId);
    }

    @PostMapping
    public Customer saveCustomer(@RequestBody CustomerRegistrationRequest request) {
        return this.customerService.saveCustomer(request);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable(name = "id") Integer id) {
        this.customerService.deleteCustomerById(id);
    }
}
