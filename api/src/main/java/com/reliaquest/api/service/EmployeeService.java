package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.CreateEmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeApiClient employeeApiClient;

    public List<Employee> getAllEmployees() {
        return employeeApiClient.getAllEmployees();
    }

    public Employee getEmployeeById(String id) {
        return employeeApiClient.getEmployeeById(id);
    }

    public List<Employee> searchEmployeesByName(String nameFragment) {
        return employeeApiClient.getAllEmployees().stream()
                .filter(e -> e.getEmployee_name() != null &&
                        e.getEmployee_name().toLowerCase().contains(nameFragment.toLowerCase()))
                .collect(Collectors.toList());
    }

    public int getHighestSalary() {
        return employeeApiClient.getAllEmployees().stream()
                .mapToInt(Employee::getEmployee_salary)
                .max()
                .orElse(0);
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        return employeeApiClient.getAllEmployees().stream()
                .sorted(Comparator.comparingInt(Employee::getEmployee_salary).reversed())
                .limit(10)
                .map(Employee::getEmployee_name)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(CreateEmployeeRequest request) {
        return employeeApiClient.createEmployee(request);
    }

    public String deleteEmployeeByName(String name) {
        return employeeApiClient.deleteEmployeeByName(name);
    }
}