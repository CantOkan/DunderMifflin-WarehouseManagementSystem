package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.EmployeeDto;
import com.canok.whmanagement.entity.Employee;
import com.canok.whmanagement.entity.Order;
import com.canok.whmanagement.entity.Role;
import com.canok.whmanagement.repository.ClientRepository;
import com.canok.whmanagement.repository.EmployeeRepository;
import com.canok.whmanagement.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDto dto;

    @BeforeEach
    void setUp() {
        dto = EmployeeDto.builder()
                .firstName("John").surName("Doe").gender('M')
                .username("jdoe").password("secret").role(Role.WAREHOUSE_WORKER)
                .build();
    }

    @Test
    void registerEmployee_shouldEncodePasswordAndSave() {
        when(employeeRepository.existsByUsername("jdoe")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("ENC");
        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.registerEmployee(dto);

        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isEqualTo("ENC");
        assertThat(result.getUsername()).isEqualTo("jdoe");
    }

    @Test
    void registerEmployee_shouldReturnNull_whenUsernameOrPasswordMissing() {
        EmployeeDto invalid = EmployeeDto.builder().username(null).password("p").build();
        assertThat(employeeService.registerEmployee(invalid)).isNull();
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void registerEmployee_shouldReturnNull_whenUsernameAlreadyExists() {
        when(employeeRepository.existsByUsername("jdoe")).thenReturn(true);
        assertThat(employeeService.registerEmployee(dto)).isNull();
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void findEmployeeById_shouldReturnEmployee_whenFound() {
        Employee e = Employee.builder().id(5L).username("u").build();
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(e));
        assertThat(employeeService.findEmployeeById(5L)).isSameAs(e);
    }

    @Test
    void findEmployeeById_shouldReturnNull_whenMissing() {
        when(employeeRepository.findById(5L)).thenReturn(Optional.empty());
        assertThat(employeeService.findEmployeeById(5L)).isNull();
    }

    @Test
    void updateEmployee_shouldReturnNull_whenNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThat(employeeService.updateEmployee(1L, dto)).isNull();
    }

    @Test
    void updateEmployee_shouldRejectDuplicateUsername() {
        Employee existing = Employee.builder().id(1L).username("old").build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThat(employeeService.updateEmployee(1L, dto)).isNull();
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_shouldUpdateFields() {
        Employee existing = Employee.builder().id(1L).username("jdoe").password("old").build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("secret")).thenReturn("ENC");
        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.updateEmployee(1L, dto);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getPassword()).isEqualTo("ENC");
        assertThat(result.getRole()).isEqualTo(Role.WAREHOUSE_WORKER);
    }

    @Test
    void deleteEmployee_shouldReturnFalse_whenMissing() {
        when(employeeRepository.existsById(1L)).thenReturn(false);
        assertThat(employeeService.deleteEmployee(1L)).isFalse();
        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    void deleteEmployee_shouldDelete_whenExists() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        assertThat(employeeService.deleteEmployee(1L)).isTrue();
        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void findOrdersByClientId_shouldReturnEmpty_whenClientMissing() {
        when(clientRepository.existsById(7L)).thenReturn(false);
        assertThat(employeeService.findOrdersByClientId(7L)).isEmpty();
        verify(orderRepository, never()).findByClientId(any());
    }

    @Test
    void findOrdersByClientId_shouldReturnOrders_whenClientExists() {
        when(clientRepository.existsById(7L)).thenReturn(true);
        List<Order> orders = List.of(Order.builder().id("o1").clientId(7L).build());
        when(orderRepository.findByClientId(7L)).thenReturn(orders);

        assertThat(employeeService.findOrdersByClientId(7L)).hasSize(1);
    }
}