package com.canok.whmanagement.security;

import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.entity.Employee;
import com.canok.whmanagement.repository.ClientRepository;
import com.canok.whmanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsername(username).orElse(null);
        if (employee != null) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()));
            return new AuthenticatedUser(
                    employee.getId(),
                    employee.getUsername(),
                    employee.getPassword(),
                    AccountType.EMPLOYEE,
                    authorities
            );
        }

        Client client = clientRepository.findByUsername(username).orElse(null);
        if (client != null) {
            return new AuthenticatedUser(
                    client.getId(),
                    client.getUsername(),
                    client.getPassword(),
                    AccountType.CLIENT,
                    List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
            );
        }

        throw new UsernameNotFoundException("No user found with username: " + username);
    }
}
