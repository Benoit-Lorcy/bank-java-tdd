package com.example.project;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceAccountTest {

    private static BankService bankService;
    private static final String TEST_ACCOUNT_ID = "testAccount";
    private static final float INITIAL_BALANCE = 100f;
    private static final String OWNER_ID = "testUser";

    @BeforeAll
    static void setup() throws SQLException {
        bankService = new BankService();
    }

    @Test
    @Order(1)
    void testAddAccount() throws SQLException {
        Account testAccount = new Account(TEST_ACCOUNT_ID, INITIAL_BALANCE, OWNER_ID);
        bankService.addAccount(testAccount);
        Optional<Account> foundAccount = bankService.findAccount(TEST_ACCOUNT_ID);
        assertTrue(foundAccount.isPresent());
        assertEquals(INITIAL_BALANCE, foundAccount.get().getBalance());
        assertEquals(OWNER_ID, foundAccount.get().getOwnerId());
    }

    @Test
    @Order(2)
    void testFindAccount() throws SQLException {
        Optional<Account> foundAccount = bankService.findAccount(TEST_ACCOUNT_ID);
        assertTrue(foundAccount.isPresent());
        assertEquals(TEST_ACCOUNT_ID, foundAccount.get().getAccountId());
    }

    @Test
    @Order(3)
    void testUpdateAccount() throws SQLException {
        Account updatedAccount = new Account(TEST_ACCOUNT_ID, 200f, OWNER_ID);
        bankService.updateAccount(updatedAccount);
        Optional<Account> foundAccount = bankService.findAccount(TEST_ACCOUNT_ID);
        assertTrue(foundAccount.isPresent());
        assertEquals(200f, foundAccount.get().getBalance());
    }

    @Test
    @Order(4)
    void testRemoveAccount() throws SQLException {
        bankService.removeAccount(TEST_ACCOUNT_ID);
        Optional<Account> foundAccount = bankService.findAccount(TEST_ACCOUNT_ID);
        assertFalse(foundAccount.isPresent());
    }

    @AfterAll
    static void tearDown() throws SQLException {
        bankService.disconnect();
    }
}