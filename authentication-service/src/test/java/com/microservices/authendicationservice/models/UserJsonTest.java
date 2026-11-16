package com.microservices.authendicationservice.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/** GET /auth/get-loggedInUserById returns User as JSON; the password hash must never be in it. */
class UserJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void passwordHashIsNotSerialized() throws Exception {
        User user = new User("jdoe", "jane@acme.com", "$2a$12$hashhashhash");

        String json = mapper.writeValueAsString(user);

        assertFalse(json.contains("password"), json);
        assertFalse(json.contains("$2a$12$hashhashhash"), json);
        assertTrue(json.contains("jane@acme.com"), json);
    }

    @Test
    void passwordIsStillAvailableToJavaCode() {
        User user = new User("jdoe", "jane@acme.com", "$2a$12$hashhashhash");
        assertEquals("$2a$12$hashhashhash", user.getPassword()); // used by UserDetailsImpl at sign-in
    }
}
