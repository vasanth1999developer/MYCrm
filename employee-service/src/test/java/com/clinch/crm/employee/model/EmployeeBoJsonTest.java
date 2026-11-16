package com.clinch.crm.employee.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/** POST /employee/create-employee returns the EmployeeBo it received; the password must not be echoed. */
class EmployeeBoJsonTest {

    /** Jackson with Java time support (for {@code dateOfJoining}). */
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * {@code WRITE_ONLY}: the password is read from JSON but never written back.
     *
     * @throws Exception if JSON parsing fails
     */
    @Test
    void passwordIsAcceptedButNeverReturned() throws Exception {
        EmployeeBo bo = mapper.readValue(
                "{\"username\":\"jdoe\",\"email\":\"jane@acme.com\",\"password\":\"Secret123\",\"firstName\":\"Jane\"}",
                EmployeeBo.class);
        assertEquals("Secret123", bo.getPassword(), "must still reach authentication-service");

        String json = mapper.writeValueAsString(bo);
        assertFalse(json.contains("password"), json);
        assertFalse(json.contains("Secret123"), json);
    }
}
