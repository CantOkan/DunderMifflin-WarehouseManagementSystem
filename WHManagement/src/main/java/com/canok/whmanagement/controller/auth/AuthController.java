package com.canok.whmanagement.controller.auth;

import com.canok.whmanagement.dto.ClientDto;
import com.canok.whmanagement.dto.EmployeeDto;
import com.canok.whmanagement.dto.LoginRequestDto;
import com.canok.whmanagement.dto.LoginResponseDto;
import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.entity.Employee;
import com.canok.whmanagement.security.ApplicationUserDetailsService;
import com.canok.whmanagement.security.AuthenticatedUser;
import com.canok.whmanagement.security.JwtService;
import com.canok.whmanagement.service.ClientService;
import com.canok.whmanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final ApplicationUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final ClientService clientService;
    private final EmployeeService employeeService;

    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody ClientDto clientDto) {
        Client created = clientService.registerClient(clientDto);
        if (created == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Client registration failed."));
        }
        return ResponseEntity.ok(created);
    }

    @PostMapping("/register/employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerEmployee(@RequestBody EmployeeDto employeeDto) {
        Employee created = employeeService.registerEmployee(employeeDto);
        if (created == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Employee registration failed."));
        }
        return ResponseEntity.ok(created);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password."));
        }

        AuthenticatedUser user = (AuthenticatedUser) userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(LoginResponseDto.builder()
                .token(token)
                .accountId(user.getAccountId())
                .username(user.getUsername())
                .accountType(user.getAccountType())
                .build());
    }
}