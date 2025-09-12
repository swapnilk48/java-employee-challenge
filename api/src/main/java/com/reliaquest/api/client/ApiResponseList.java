package com.reliaquest.api.client;

import com.reliaquest.api.model.Employee;
import lombok.Data;

import java.util.List;

@Data
public class ApiResponseList {
    private List<Employee> data;
}