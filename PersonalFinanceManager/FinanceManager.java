package net.javaguides.pfm;

import java.sql.*;

public class FinanceManager {
    private final String url = "jdbc:mysql://localhost:3306/finance_db";
    private final String user = "root"; // Use your MySQL username
    private final String password = "root"; // Use your MySQL password

    public FinanceManager() {
        // Test the connection
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database.");
            e.printStackTrace();
        }
    }

    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (type, amount, description) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transaction.getType());
            statement.setDouble(2, transaction.getAmount());
            statement.setString(3, transaction.getDescription());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding transaction.");
            e.printStackTrace();
        }
    }

    public void editTransaction(int id, String type, double amount, String description) {
        String sql = "UPDATE transactions SET type = ?, amount = ?, description = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type);
            statement.setDouble(2, amount);
            statement.setString(3, description);
            statement.setInt(4, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error editing transaction.");
            e.printStackTrace();
        }
    }

    public void deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting transaction.");
            e.printStackTrace();
        }
    }

    public void listTransactions() {
        String sql = "SELECT * FROM transactions";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");
                double amount = resultSet.getDouble("amount");
                String description = resultSet.getString("description");
                System.out.println(new Transaction(id, type, amount, description));
            }
        } catch (SQLException e) {
            System.out.println("Error listing transactions.");
            e.printStackTrace();
        }
    }

    public void summarizeTransactions() {
        String sql = "SELECT type, SUM(amount) AS total FROM transactions GROUP BY type";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            double incomeTotal = 0;
            double expenseTotal = 0;
            while (resultSet.next()) {
                String type = resultSet.getString("type");
                double total = resultSet.getDouble("total");
                if (type.equalsIgnoreCase("Income")) {
                    incomeTotal += total;
                } else if (type.equalsIgnoreCase("Expense")) {
                    expenseTotal += total;
                }
            }
            System.out.println("Total Income: INR " + incomeTotal);
            System.out.println("Total Expenses: INR " + expenseTotal);
            System.out.println("Net Flow: INR " + (incomeTotal - expenseTotal));
        } catch (SQLException e) {
            System.out.println("Error summarizing transactions.");
            e.printStackTrace();
        }
    }

    public int getNextId() {
        String sql = "SELECT MAX(id) AS max_id FROM transactions";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            System.out.println("Error getting next transaction ID.");
            e.printStackTrace();
        }
        return 1;
    }
}
