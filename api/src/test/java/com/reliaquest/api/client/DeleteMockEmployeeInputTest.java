package com.reliaquest.api.client;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeleteMockEmployeeInputTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testConstructorAndGetter() {
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput("John Doe");
        assertEquals("John Doe", input.getName());
    }

    @Test
    void testSetter() {
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput();
        input.setName("Jane Doe");
        assertEquals("Jane Doe", input.getName());
    }

    @Test
    void testNotBlankValidation() {
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput("");
        Set<ConstraintViolation<DeleteMockEmployeeInput>> violations = validator.validate(input);
        assertFalse(violations.isEmpty());

        input.setName("Valid Name");
        violations = validator.validate(input);
        assertTrue(violations.isEmpty());
    }
}
