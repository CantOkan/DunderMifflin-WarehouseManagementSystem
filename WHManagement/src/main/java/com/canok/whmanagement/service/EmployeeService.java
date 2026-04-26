package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.EmployeeDto;
import com.canok.whmanagement.entity.Employee;
import com.canok.whmanagement.entity.Order;
import com.canok.whmanagement.repository.ClientRepository;
import com.canok.whmanagement.repository.EmployeeRepository;
import com.canok.whmanagement.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public Employee createEmployee(EmployeeDto employeeDto) {
        return registerEmployee(employeeDto);
    }

    public Employee registerEmployee(EmployeeDto employeeDto) {
        if (employeeDto.getUsername() == null || employeeDto.getPassword() == null) {
            return null;
        }
        if (employeeRepository.existsByUsername(employeeDto.getUsername())) {
            return null;
        }
        Employee employee = employeeDto.convertToEmployee();
        employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        return employeeRepository.save(employee);
    }

    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Long id, EmployeeDto employeeDto) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        Employee employee = optional.get();
        employee.setFirstName(employeeDto.getFirstName());
        employee.setSurName(employeeDto.getSurName());
        employee.setGender(employeeDto.getGender());
        if (employeeDto.getUsername() != null) {
            if (!employee.getUsername().equals(employeeDto.getUsername()) &&
                    employeeRepository.existsByUsername(employeeDto.getUsername())) {
                return null;
            }
            employee.setUsername(employeeDto.getUsername());
        }
        if (employeeDto.getPassword() != null) {
            employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        }
        employee.setRole(employeeDto.getRole());
        return employeeRepository.save(employee);
    }

    public Boolean deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            return false;
        }
        employeeRepository.deleteById(id);
        return true;
    }

    public List<Order> findOrdersByClientId(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            return List.of();
        }
        return orderRepository.findByClientId(clientId);
    }
}
