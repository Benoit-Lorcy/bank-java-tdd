package com.example.project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankService {

    private static final String URL = "jdbc:sqlite:bank.db";
    private Connection conn;

    public BankService() {
        connect();
        initializeDatabase();
    }

    private void connect() {
        try {
            this.conn = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (this.conn != null) {
                this.conn.close();
                System.out.println("Connection to SQLite has been closed.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initializeDatabase() {
        try (Statement stmt = this.conn.createStatement()) {
            // SQL statement for creating a new table for users
            String sqlUserTable = "CREATE TABLE IF NOT EXISTS users (\n"
                    + " id text PRIMARY KEY,\n"
                    + " password text NOT NULL,\n"
                    + " role text NOT NULL\n"
                    + ");";

            // SQL statement for creating a new table for accounts
            String sqlAccountTable = "CREATE TABLE IF NOT EXISTS accounts (\n"
                    + " accountId text PRIMARY KEY,\n"
                    + " balance real NOT NULL,\n"
                    + " ownerId text NOT NULL,\n"
                    + " FOREIGN KEY (ownerId) REFERENCES users (id)\n"
                    + ");";

            // SQL statement for creating a new table for checks
            String sqlCheckTable = "CREATE TABLE IF NOT EXISTS checks (\n"
                    + " checkId text PRIMARY KEY,\n"
                    + " amount real NOT NULL,\n"
                    + " accountId text NOT NULL,\n"
                    + " isValidated boolean NOT NULL,\n"
                    + " FOREIGN KEY (accountId) REFERENCES accounts (accountId)\n"
                    + ");";

            // Execute SQL statements
            stmt.execute(sqlUserTable);
            stmt.execute(sqlAccountTable);
            stmt.execute(sqlCheckTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // User management methods
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (id, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        }
    }

    public void removeUser(String userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.executeUpdate();
        }
    }

    public Optional<User> findUser(String userId) throws SQLException {
        String sql = "SELECT id, password, role FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getString("id"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public boolean authenticateUser(String userId, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE id = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET password = ?, role = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getRole());
            pstmt.setString(3, user.getId());
            pstmt.executeUpdate();
        }
    }


    //Account management methods
    public void addAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (accountId, balance, ownerId) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountId());
            pstmt.setDouble(2, account.getBalance());
            pstmt.setString(3, account.getOwnerId());
            pstmt.executeUpdate();
        }
    }

    public void removeAccount(String accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE accountId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            pstmt.executeUpdate();
        }
    }

    public Optional<Account> findAccount(String accountId) throws SQLException {
        String sql = "SELECT accountId, balance, ownerId FROM accounts WHERE accountId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Account account = new Account(
                        rs.getString("accountId"),
                        rs.getFloat("balance"),
                        rs.getString("ownerId")
                );
                return Optional.of(account);
            }
        }
        return Optional.empty();
    }

    public void updateAccount(Account account) throws SQLException {
        String sql = "UPDATE accounts SET balance = ?, ownerId = ? WHERE accountId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, account.getBalance());
            pstmt.setString(2, account.getOwnerId());
            pstmt.setString(3, account.getAccountId());
            pstmt.executeUpdate();
        }
    }

    public void depositAmount(String accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE accountId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountId);
            pstmt.executeUpdate();
        }
    }

    public void withdrawAmount(String accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE accountId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountId);
            pstmt.executeUpdate();
        }
    }

    public void transferAmount(String fromAccountId, String toAccountId, double amount) throws SQLException {
        // Check if both accounts exist and have sufficient funds
        Optional<Account> fromAccount = findAccount(fromAccountId);
        Optional<Account> toAccount = findAccount(toAccountId);

        if (fromAccount.isPresent() && toAccount.isPresent() && fromAccount.get().getBalance() >= amount) {
            // Withdraw from the source account
            withdrawAmount(fromAccountId, amount);
            // Deposit to the destination account
            depositAmount(toAccountId, amount);
        } else {
            // Throw an exception or handle it as per your application's requirements
            throw new SQLException("Transfer cannot be completed: accounts validation failed or insufficient funds.");
        }
    }

    public void depositCheck(String checkId, String accountId, double amount) throws SQLException {
        String sql = "INSERT INTO checks (checkId, amount, accountId, isValidated) VALUES (?, ?, ?, false)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, checkId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, accountId);
            pstmt.executeUpdate();
        }
    }

    public void validateCheck(String checkId) throws SQLException {
        // First, retrieve the check details
        String findCheckSql = "SELECT amount, accountId FROM checks WHERE checkId = ? AND isValidated = false";
        try (PreparedStatement pstmtFind = conn.prepareStatement(findCheckSql)) {
            pstmtFind.setString(1, checkId);
            ResultSet rs = pstmtFind.executeQuery();

            if (rs.next()) {
                double amount = rs.getDouble("amount");
                String accountId = rs.getString("accountId");

                // Now validate the check and update the account balance
                String validateSql = "UPDATE checks SET isValidated = true WHERE checkId = ?";
                try (PreparedStatement pstmtValidate = conn.prepareStatement(validateSql)) {
                    pstmtValidate.setString(1, checkId);
                    pstmtValidate.executeUpdate();
                }

                // Update the account balance
                depositAmount(accountId, amount);
            } else {
                // Handle the case where the check does not exist or is already validated
                throw new SQLException("Check validation failed: Check does not exist or is already validated.");
            }
        }
    }

    public List<Account> getAccountsForUser(String userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT accountId, balance, ownerId FROM accounts WHERE ownerId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = new Account(
                        rs.getString("accountId"),
                        rs.getFloat("balance"),
                        rs.getString("ownerId")
                );
                accounts.add(account);
            }
        }
        return accounts;
    }

    public List<Check> getUnvalidatedChecksForAccount(String accountId) throws SQLException {
        List<Check> checks = new ArrayList<>();
        String sql = "SELECT checkId, amount, accountId, isValidated FROM checks WHERE accountId = ? AND isValidated = false";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Check check = new Check(
                        rs.getString("checkId"),
                        rs.getFloat("amount"),
                        rs.getString("accountId"),
                        rs.getBoolean("isValidated")
                );
                checks.add(check);
            }
        }
        return checks;
    }

    //Method  only used to clear the database
    public void removeCheck(String checkId) throws SQLException {
        String sql = "DELETE FROM checks WHERE checkId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, checkId);
            pstmt.executeUpdate();
        }
    }
}


