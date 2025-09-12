package com.reliaquest.api.client;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.CreateEmployeeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeApiClient {

    private final WebClient employeeWebClient;

    public List<Employee> getAllEmployees() {
        return employeeWebClient.get()
                .retrieve()
                .bodyToMono(ApiResponseList.class)
                .map(ApiResponseList::getData)
                .block();
    }

    public Employee getEmployeeById(String id) {
        return employeeWebClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(ApiResponse::getData)
                .block();
    }

    public Employee createEmployee(CreateEmployeeRequest request) {
        return employeeWebClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(ApiResponse::getData)
                .block();
    }

    public String deleteEmployeeByName(Employee emp) {
        String name = emp.getEmployee_name();

        DeleteMockEmployeeInput body = new DeleteMockEmployeeInput(name);
        return employeeWebClient
                .method(HttpMethod.DELETE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(resp -> name)
                .block();
    }
}
