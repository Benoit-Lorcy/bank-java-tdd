package com.example.project;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceCheckTest {

    private static BankService bankService;
    private static final String TEST_USER_ID = "testUser";
    private static final String TEST_ACCOUNT_ID = "testAccount";
    private static final String TEST_CHECK_ID_1 = "testCheck1";
    private static final String TEST_CHECK_ID_2 = "testCheck2";
    private static final float INITIAL_BALANCE = 100.0f;
    private static final float CHECK_AMOUNT = 50.0f;

    @BeforeAll
    static void setup() throws SQLException {
        bankService = new BankService();

        // Creating test user and account
        bankService.addUser(new User(TEST_USER_ID, "password", "client"));
        bankService.addAccount(new Account(TEST_ACCOUNT_ID, INITIAL_BALANCE, TEST_USER_ID));
    }

    @Test
    @Order(1)
    void testDepositCheck() throws SQLException {
        bankService.depositCheck(TEST_CHECK_ID_1, TEST_ACCOUNT_ID, CHECK_AMOUNT);
        List<Check> checks = bankService.getUnvalidatedChecksForAccount(TEST_ACCOUNT_ID);
        assertTrue(checks.stream().anyMatch(check -> check.getCheckId().equals(TEST_CHECK_ID_1)));
    }

    @Test
    @Order(2)
    void testValidateCheck() throws SQLException {
        bankService.validateCheck(TEST_CHECK_ID_1);
        Optional<Account> account = bankService.findAccount(TEST_ACCOUNT_ID);
        assertTrue(account.isPresent());
        assertEquals(INITIAL_BALANCE + CHECK_AMOUNT, account.get().getBalance());
    }

    @Test
    @Order(3)
    void testGetUnvalidatedChecksForAccount() throws SQLException {
        bankService.depositCheck(TEST_CHECK_ID_2, TEST_ACCOUNT_ID, CHECK_AMOUNT);
        List<Check> checks = bankService.getUnvalidatedChecksForAccount(TEST_ACCOUNT_ID);
        assertTrue(checks.stream().anyMatch(check -> check.getCheckId().equals(TEST_CHECK_ID_2) && !check.getIsValidated()));
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // Clean up test data
        bankService.removeAccount(TEST_ACCOUNT_ID);
        bankService.removeUser(TEST_USER_ID);
        // Assuming you have a method to remove checks
        bankService.removeCheck(TEST_CHECK_ID_1);
        bankService.removeCheck(TEST_CHECK_ID_2);
        bankService.disconnect();
    }
}
