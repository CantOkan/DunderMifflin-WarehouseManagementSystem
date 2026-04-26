package com.canok.whmanagement.dto;

import com.canok.whmanagement.entity.Employee;
import com.canok.whmanagement.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {

    private String firstName;
    private String surName;
    private Character gender;
    private String username;
    private String password;
    private Role role;

    public Employee convertToEmployee() {
        return Employee.builder()
                .firstName(firstName)
                .surName(surName)
                .gender(gender)
                .username(username)
                .password(password)
                .role(role)
                .build();
    }
}
