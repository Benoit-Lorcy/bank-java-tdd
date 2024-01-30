package com.example.project;

import com.example.project.Account;
import com.example.project.BankService;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceTransactionTest {

    private static BankService bankService;
    private static final String TEST_ACCOUNT_ID_1 = "testAccount1";
    private static final String TEST_ACCOUNT_ID_2 = "testAccount2";
    private static final float INITIAL_BALANCE = 100f;
    private static final String OWNER_ID = "testUser";

    @BeforeAll
    static void setup() throws SQLException {
        bankService = new BankService();

        // Creating test accounts
        bankService.addAccount(new Account(TEST_ACCOUNT_ID_1, INITIAL_BALANCE, OWNER_ID));
        bankService.addAccount(new Account(TEST_ACCOUNT_ID_2, INITIAL_BALANCE, OWNER_ID));
    }

    @AfterAll
    static void tearDown() throws SQLException {
        bankService.removeAccount(TEST_ACCOUNT_ID_1);
        bankService.removeAccount(TEST_ACCOUNT_ID_2);
    }

    @Test
    @Order(1)
    void testDepositAmount() throws SQLException {
        bankService.depositAmount(TEST_ACCOUNT_ID_1, 50f);
        Optional<Account> account = bankService.findAccount(TEST_ACCOUNT_ID_1);
        assertTrue(account.isPresent());
        assertEquals(150f, account.get().getBalance());
    }

    @Test
    @Order(2)
    void testWithdrawAmount() throws SQLException {
        bankService.withdrawAmount(TEST_ACCOUNT_ID_1, 30f);
        Optional<Account> account = bankService.findAccount(TEST_ACCOUNT_ID_1);
        assertTrue(account.isPresent());
        assertEquals(120f, account.get().getBalance());
    }

    @Test
    @Order(3)
    void testTransferAmount() throws SQLException {
        bankService.transferAmount(TEST_ACCOUNT_ID_1, TEST_ACCOUNT_ID_2, 20f);
        Optional<Account> account1 = bankService.findAccount(TEST_ACCOUNT_ID_1);
        Optional<Account> account2 = bankService.findAccount(TEST_ACCOUNT_ID_2);

        assertTrue(account1.isPresent());
        assertTrue(account2.isPresent());
        assertEquals(100f, account1.get().getBalance());
        assertEquals(120f, account2.get().getBalance());
    }


}
