package com.reliaquest.api.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Employee {
    private UUID id;
    private String employee_name;
    private Integer employee_salary;
    private Integer employee_age;
    private String employee_title;
    private String employee_email;
}
