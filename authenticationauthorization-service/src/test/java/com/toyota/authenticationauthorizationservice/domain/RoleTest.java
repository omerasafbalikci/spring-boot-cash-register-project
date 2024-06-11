package com.toyota.authenticationauthorizationservice.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "role", "description", List.of(new User()));
    }

    @Test
    void getId() {
        assertEquals(1L, role.getId());
    }

    @Test
    void getName() {
        assertEquals("role", role.getName());
    }

    @Test
    void getDescription() {
        assertEquals("description", role.getDescription());
    }

    @Test
    void getUsers() {
        assertNotNull(role.getUsers());
    }

    @Test
    void setId() {
        Long id = 2L;
        role.setId(id);
        assertEquals(id, role.getId());
    }

    @Test
    void setName() {
        String name = "test";
        role.setName(name);
        assertEquals(name, role.getName());
    }

    @Test
    void setDescription() {
        String description = "test";
        role.setDescription(description);
        assertEquals(description, role.getDescription());
    }

    @Test
    void setUsers() {
        role.setUsers(null);
        assertNull(role.getUsers());
    }
}
