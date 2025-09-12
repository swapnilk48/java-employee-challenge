package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService service;

    @Autowired
    private ObjectMapper mapper;

    private final Employee sample = new Employee(
            UUID.randomUUID(),
            "Alice",
            100000,
            30,
            "Engineer",
            "alice@company.com"
    );

    @Nested
    @DisplayName("GET /api/v1/employees")
    class GetAll {
        @Test
        void returnsList() throws Exception {
            Mockito.when(service.getAllEmployees()).thenReturn(List.of(sample));

            mvc.perform(get("/api/v1/employees"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(sample.getId().toString()));
        }

        @Test
        void returns404WhenEmpty() throws Exception {
            Mockito.when(service.getAllEmployees())
                    .thenThrow(new EmployeeNotFoundException("No employees found"));

            mvc.perform(get("/api/v1/employees"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("No employees found"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employees/{id}")
    class GetById {
        @Test
        void returnsEmployee() throws Exception {
            Mockito.when(service.getEmployeeById(sample.getId().toString())).thenReturn(sample);

            mvc.perform(get("/api/v1/employees/{id}", sample.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(sample.getId().toString()))
                    .andExpect(jsonPath("$.employee_name").value(sample.getEmployee_name()));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            Mockito.when(service.getEmployeeById(sample.getId().toString()))
                    .thenThrow(new EmployeeNotFoundException("Employee not found with ID: " + sample.getId().toString()));

            mvc.perform(get("/api/v1/employees/{id}", sample.getId().toString()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Employee not found with ID: " + sample.getId()));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/employees")
    class Create {
        @Test
        void createsEmployee() throws Exception {
            CreateEmployeeRequest req = new CreateEmployeeRequest("Bob", 80000, 28, "Developer");
            Employee created = new Employee(
                    UUID.randomUUID(),
                    "Bob",
                    80000,
                    28,
                    "Developer",
                    "bob@company.com"
            );

            Mockito.when(service.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(created);

            mvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/employees/" + created.getId().toString()))
                    .andExpect(jsonPath("$.id").value(created.getId().toString()))
                    .andExpect(jsonPath("$.employee_name").value("Bob"));
        }

        @Test
        void returns400OnInvalid() throws Exception {
            // missing name
            CreateEmployeeRequest req = new CreateEmployeeRequest("", 0, 0, "");

            mvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/employees/{id}")
    class DeleteById {
        @Test
        void deletesEmployee() throws Exception {
            Mockito.when(service.getEmployeeById(sample.getId().toString())).thenReturn(sample);
            Mockito.when(service.deleteEmployeeByName(sample)).thenReturn(sample.getEmployee_name());

            mvc.perform(delete("/api/v1/employees/{id}", sample.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(sample.getEmployee_name()));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            Mockito.when(service.getEmployeeById(sample.getId().toString()))
                    .thenThrow(new EmployeeNotFoundException("Employee not found with ID: " + sample.getId().toString()));

            mvc.perform(delete("/api/v1/employees/{id}", sample.getId().toString()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Employee not found with ID: " + sample.getId().toString()));
        }
    }
}
