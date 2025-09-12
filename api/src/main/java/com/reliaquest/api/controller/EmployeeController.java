package com.reliaquest.api.controller;

import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/employees")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {

    private final EmployeeService employeeService;

    @GetMapping
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("GET /employees - Fetching all employees");
        List<Employee> employees = employeeService.getAllEmployees();

        if (employees == null) {
            log.warn("Employee service returned null");
            throw new EmployeeNotFoundException("No employees available");
        }

        log.info("Returned {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/search/{searchString}")
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("GET /employees/search/{} - Searching employees by name", searchString);

        if (searchString.trim().isEmpty()) {
            log.warn("Search string is empty");
            throw new BadRequestException("Search string cannot be empty");
        }

        List<Employee> employees = employeeService.searchEmployeesByName(searchString);

        if (employees == null) {
            log.warn("Employee service returned null for search: {}", searchString);
            throw new EmployeeNotFoundException("No employees found matching: " + searchString);
        }

        log.info("Found {} employees matching '{}'", employees.size(), searchString);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        log.info("GET /employees/{} - Fetching employee by ID", id);

        if (id.trim().isEmpty()) {
            log.warn("Employee ID is empty");
            throw new BadRequestException("Employee ID cannot be empty");
        }

        Employee employee = employeeService.getEmployeeById(id);

        if (employee == null) {
            log.warn("Employee with ID {} not found", id);
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        }

        log.info("Found employee: {}", employee.getEmployee_name());
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/highestSalary")
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("GET /employees/highestSalary - Fetching highest salary");
        int highestSalary = employeeService.getHighestSalary();

        if (highestSalary <= 0) {
            log.warn("No valid salary data found");
            throw new EmployeeNotFoundException("No employees found to calculate highest salary");
        }

        log.info("Highest salary found: {}", highestSalary);
        return ResponseEntity.ok(highestSalary);
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("GET /employees/topTenHighestEarningEmployeeNames - Fetching top 10 earning employee names");
        List<String> topEarners = employeeService.getTop10HighestEarningEmployeeNames();

        if (topEarners == null || topEarners.isEmpty()) {
            log.warn("No employees found for top earners calculation");
            throw new EmployeeNotFoundException("No employees found to calculate top earners");
        }

        log.info("Returned top 10 highest earning employee names");
        return ResponseEntity.ok(topEarners);
    }

    @PostMapping
    @Override
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody CreateEmployeeRequest employeeInput) {
        log.info("POST /employees - Creating employee with name: {}", employeeInput.getName());

        Employee createdEmployee = employeeService.createEmployee(employeeInput);

        if (createdEmployee == null) {
            log.error("Employee service returned null after creation");
            throw new RuntimeException("Failed to create employee");
        }

        log.info("Employee created with ID: {}", createdEmployee.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/v1/employees/" + createdEmployee.getId())
                .body(createdEmployee);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        log.info("DELETE /employees/{} - Fetching employee details", id);

        if (id.trim().isEmpty()) {
            log.warn("Employee ID is empty for deletion");
            throw new BadRequestException("Employee ID cannot be empty");
        }

        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        }

        log.info("Deleting employee with name: {}", employee.getEmployee_name());
        String deleted = employeeService.deleteEmployeeByName(employee);

        if (deleted == null || deleted.trim().isEmpty()) {
            log.error("Delete operation failed for employee with ID: {}", id);
            throw new RuntimeException("Failed to delete employee with ID: " + id);
        }

        log.info("Employee deleted with ID: {}", id);
        return ResponseEntity.ok(deleted);
    }
}
