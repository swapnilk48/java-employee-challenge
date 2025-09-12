package com.reliaquest.api.model;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateEmployeeRequest {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Salary is required")
    @Min(value = 1, message = "Salary must be greater than 0")
    private Integer salary;

    @NotNull(message = "Age is required")
    @Min(value = 16, message = "Minimum age is 16")
    @Max(value = 75, message = "Maximum age is 75")
    private Integer age;

    @NotBlank(message = "Title must not be blank")
    private String title;
}