package com.canok.whmanagement.controller;

import com.canok.whmanagement.dto.EmployeeDto;
import com.canok.whmanagement.entity.Employee;
import com.canok.whmanagement.entity.Order;
import com.canok.whmanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDto employeeDto) {
        Employee result = employeeService.createEmployee(employeeDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> findEmployeeById(@PathVariable Long id) {
        Employee result = employeeService.findEmployeeById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> findAllEmployees() {
        List<Employee> result = employeeService.findAllEmployees();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        Employee result = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        Boolean result = employeeService.deleteEmployee(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/client/{clientId}/orders")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> findOrdersByClientId(@PathVariable Long clientId) {
        List<Order> orders = employeeService.findOrdersByClientId(clientId);
        return ResponseEntity.ok(orders);
    }
}
