package com.example.project;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceAccountListTest {
    private static BankService bankService;
    private static final String TEST_USER_ID = "testUser";
    private static final String TEST_ACCOUNT_ID_1 = "testAccount1";
    private static final String TEST_ACCOUNT_ID_2 = "testAccount2";
    private static final float INITIAL_BALANCE = 100.0f;

    @BeforeAll
    static void setup() throws SQLException {
        bankService = new BankService();

        // Creating test user and accounts
        bankService.addUser(new User(TEST_USER_ID, "password", "client"));
        bankService.addAccount(new Account(TEST_ACCOUNT_ID_1, INITIAL_BALANCE, TEST_USER_ID));
        bankService.addAccount(new Account(TEST_ACCOUNT_ID_2, INITIAL_BALANCE, TEST_USER_ID));
    }

    @Test
    void testGetAccountsForUser() throws SQLException {
        List<Account> accounts = bankService.getAccountsForUser(TEST_USER_ID);
        assertNotNull(accounts);
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(account -> account.getAccountId().equals(TEST_ACCOUNT_ID_1)));
        assertTrue(accounts.stream().anyMatch(account -> account.getAccountId().equals(TEST_ACCOUNT_ID_2)));
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // Clean up test data
        bankService.removeAccount(TEST_ACCOUNT_ID_1);
        bankService.removeAccount(TEST_ACCOUNT_ID_2);
        bankService.removeUser(TEST_USER_ID);
        bankService.disconnect();
    }
}
