package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeApiClient employeeApiClient;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee emp1;
    private Employee emp2;
    private Employee emp3;

    @BeforeEach
    void setUp() {
        emp1 = new Employee(UUID.randomUUID(), "Alice", 100000, 30, "Engineer", "alice@company.com");
        emp2 = new Employee(UUID.randomUUID(), "Bob", 120000, 35, "Manager", "bob@company.com");
        emp3 = new Employee(UUID.randomUUID(), "Charlie", 90000, 28, "Developer", "charlie@company.com");
    }

    @Test
    void getAllEmployees_returnsList() {
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(emp1, emp2));

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).containsExactly(emp1, emp2);
        verify(employeeApiClient).getAllEmployees();
    }

    @Test
    void getEmployeeById_returnsEmployee() {
        when(employeeApiClient.getEmployeeById("1")).thenReturn(emp1);

        Employee result = employeeService.getEmployeeById("1");

        assertThat(result).isEqualTo(emp1);
        verify(employeeApiClient).getEmployeeById("1");
    }

    @Test
    void searchEmployeesByName_filtersCorrectly() {
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(emp1, emp2, emp3));

        List<Employee> result = employeeService.searchEmployeesByName("ali");

        assertThat(result).containsExactly(emp1);
    }

    @Test
    void getHighestSalary_returnsMaxSalary() {
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(emp1, emp2, emp3));

        int result = employeeService.getHighestSalary();

        assertThat(result).isEqualTo(120000);
    }

    @Test
    void getTop10HighestEarningEmployeeNames_returnsSortedNames() {
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(emp1, emp2, emp3));

        List<String> result = employeeService.getTop10HighestEarningEmployeeNames();

        assertThat(result).containsExactly("Bob", "Alice", "Charlie");
    }

    @Test
    void createEmployee_delegatesToApiClient() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Dave", 80000, 25, "Intern");
        Employee created = new Employee(UUID.randomUUID(), "Dave", 80000, 25, "Intern", "dave@company.com");

        when(employeeApiClient.createEmployee(request)).thenReturn(created);

        Employee result = employeeService.createEmployee(request);

        assertThat(result).isEqualTo(created);
        verify(employeeApiClient).createEmployee(request);
    }

    @Test
    void deleteEmployeeByName_delegatesToApiClient() {
        when(employeeApiClient.deleteEmployeeByName(emp1)).thenReturn("Alice");

        String result = employeeService.deleteEmployeeByName(emp1);

        assertThat(result).isEqualTo("Alice");
        verify(employeeApiClient).deleteEmployeeByName(emp1);
    }
}
