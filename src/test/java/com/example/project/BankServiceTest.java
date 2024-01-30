/*
 * Copyright 2015-2023 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package com.example.project;

import org.junit.jupiter.api.*;

import java.awt.image.BandCombineOp;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceTest {

    private static BankService bankService;
    private static final String TEST_USER_ID = "testUser";
    private static final String TEST_USER_PASSWORD = "password";
    private static final String TEST_USER_ROLE = "client";

    @BeforeAll
    static void setup() throws SQLException {
        bankService = new BankService();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        bankService.disconnect();
    }

    @Test
    void testAddUser() throws SQLException {
        User user = new User("testUser", "password123", "client");
        bankService.addUser(user);

        Optional<User> foundUser = bankService.findUser("testUser");
        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        bankService.removeUser("testUser");
    }

    @Test
    void testRemoveUser() throws SQLException {
        User user = new User("testUser", "password123", "client");
        bankService.addUser(user);
        bankService.removeUser("testUser");

        Optional<User> foundUser = bankService.findUser("testUser");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testAuthenticateUser() throws SQLException {
        User user = new User("testUser", "password123", "client");
        bankService.addUser(user);

        boolean isAuthenticated = bankService.authenticateUser("testUser", "password123");
        assertTrue(isAuthenticated);
        bankService.removeUser("testUser");
    }

    @Test
    void testUpdateUser() throws SQLException {
        User user = new User("testUser", "password123", "client");
        bankService.addUser(user);

        user.setPassword("newPassword");
        bankService.updateUser(user);

        assertTrue(bankService.authenticateUser("testUser", "newPassword"));
        bankService.removeUser("testUser");
    }
}
