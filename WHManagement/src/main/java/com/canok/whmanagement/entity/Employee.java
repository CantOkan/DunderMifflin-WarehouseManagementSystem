package com.canok.whmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "mt_employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String firstName;
    private String surName;
    private Character gender;
    private String role;
}


