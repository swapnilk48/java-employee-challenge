package com.reliaquest.api.client;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteMockEmployeeInput {
    @NotBlank
    private String name;

    public DeleteMockEmployeeInput() {}

    public DeleteMockEmployeeInput(String name) {
        this.name = name;
    }
}