package mk.ukim.finki.application.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {

    private CustomerRowMapper underTest;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        this.underTest = new CustomerRowMapper();
    }

    @Test
    void mapRow() throws SQLException {
        // Given
        int rowNum = 1;

        when(this.resultSet.getLong("id")).thenReturn(2L);
        when(this.resultSet.getString("name")).thenReturn("Taylor");
        when(this.resultSet.getString("email")).thenReturn("hawk@fighters.com");
        when(this.resultSet.getInt("age")).thenReturn(0);


        // When
        Customer actualCustomer = this.underTest.mapRow(this.resultSet, rowNum);

        // Then
        Customer expectedCustomer = new Customer(
                2L, "Taylor", "hawk@fighters.com", 0
        );

        assertThat(actualCustomer).isEqualTo(expectedCustomer);
    }
}