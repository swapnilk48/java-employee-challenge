package com.reliaquest.api.client;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.CreateEmployeeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public String deleteEmployeeByName(String name) {
        return employeeWebClient.delete()
                .uri("/{name}", name)
                .retrieve()
                .bodyToMono(ApiDeleteResponse.class)
                .map(resp -> name)
                .block();
    }
}
