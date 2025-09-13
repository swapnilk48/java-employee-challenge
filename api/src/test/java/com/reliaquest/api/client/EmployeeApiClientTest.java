package com.reliaquest.api.client;

import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmployeeApiClientTest {

    @Mock private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersUriSpec requestUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EmployeeApiClient employeeApiClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEmployees() {
        Employee emp = new Employee(
                UUID.randomUUID(), "John", 90000, 32, "Developer", "john@company.com");

        ApiResponseList mockResponse = new ApiResponseList();
        mockResponse.setData(List.of(emp));

        when(webClient.get()).thenReturn(requestUriSpec);
        when(requestUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ApiResponseList.class)).thenReturn(Mono.just(mockResponse));

        List<Employee> employees = employeeApiClient.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("John", employees.get(0).getEmployee_name());
    }

    @Test
    void testGetEmployeeById() {
        Employee emp = new Employee(
                UUID.randomUUID(), "Alice", 100000, 30, "Engineer", "alice@company.com");

        ApiResponse mockResponse = new ApiResponse();
        mockResponse.setData(emp);

        when(webClient.get()).thenReturn(requestUriSpec);

        String id = emp.getId().toString();
        when(requestUriSpec.uri(eq("/{id}"), eq(id))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ApiResponse.class)).thenReturn(Mono.just(mockResponse));


        Employee result = employeeApiClient.getEmployeeById(id);

        assertNotNull(result);
        assertEquals("Alice", result.getEmployee_name());
        assertEquals("Engineer", result.getEmployee_title());
        assertEquals("alice@company.com", result.getEmployee_email());
    }


    @Test
    void testCreateEmployee() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Bob", 85000, 27, "Analyst");

        Employee emp = new Employee(
                UUID.randomUUID(), "Bob", 85000, 27, "Analyst", "bob@company.com");

        ApiResponse mockResponse = new ApiResponse();
        mockResponse.setData(emp);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ApiResponse.class)).thenReturn(Mono.just(mockResponse));

        Employee created = employeeApiClient.createEmployee(request);

        assertNotNull(created);
        assertEquals("Bob", created.getEmployee_name());
        assertEquals(85000, created.getEmployee_salary());
        assertEquals("Analyst", created.getEmployee_title());
    }

    @Test
    void testDeleteEmployeeByName() {
        Employee emp = new Employee(
                UUID.randomUUID(), "Charlie", 78000, 29, "Tester", "charlie@company.com");

        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("deleted"));

        String result = employeeApiClient.deleteEmployeeByName(emp);

        assertEquals("Charlie", result);
    }
}
