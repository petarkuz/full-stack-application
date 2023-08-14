package mk.ukim.finki.application.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        String sql = """
                SELECT id, name, email, age 
                FROM customer;
                """;

        return this.jdbcTemplate.query(sql, this.customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        String sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?
                """;

        return this.jdbcTemplate.query(sql, this.customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        String sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;

        this.jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());

        return customer;
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
//        String sql = """
//            SELECT EXISTS (
//                SELECT 1
//                FROM customer
//                WHERE email = ?
//            )
//            """;
//
//        Boolean result = this.jdbcTemplate.queryForObject(sql, Boolean.class, email);
//        return result != null && result;

        String sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;

        Integer count = this.jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Long id) {
        String sql = """
                DELETE 
                FROM customer
                WHERE id = ?
                """;

        this.jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        String sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;

        Integer count = this.jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if (customer.getName() != null) {
            String sql = """
                UPDATE customer 
                SET name = ?
                WHERE id = ?
                """;

            this.jdbcTemplate.update(sql, customer.getName(), customer.getId());
        }

        if (customer.getEmail() != null) {
            String sql = """
                UPDATE customer 
                SET email = ?
                WHERE id = ?
                """;

            this.jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
        }

        if (customer.getAge() != null) {
            String sql = """
                UPDATE customer 
                SET age = ?
                WHERE id = ?
                """;

            this.jdbcTemplate.update(sql, customer.getAge(), customer.getId());
        }

        return selectCustomerById(customer.getId()).get();
    }
}
