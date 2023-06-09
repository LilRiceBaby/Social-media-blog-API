package DAO;

import Model.Account;
import Model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocialMediaDAO {

    public boolean accountExists(String username, Connection connection) throws SQLException {
        String query = "SELECT * FROM Account WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Returns true if there's at least one record in the ResultSet, false
                                         // otherwise.
            }
        }
    }

    public Account createAccount(Account account, Connection connection) throws SQLException {
        String query = "INSERT INTO Account (username, password) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getPassword());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setAccount_id(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        }
        return account;
    }

    public Account getAccountByUsernameAndPassword(Connection connection, String username, String password)
            throws SQLException {
        String query = "SELECT * FROM Account WHERE username = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Account account = new Account();
                    account.setAccount_id(resultSet.getInt("account_id"));
                    account.setUsername(resultSet.getString("username"));
                    account.setPassword(resultSet.getString("password"));
                    return account;
                } else {
                    return null;
                }
            }
        }
    }

    public Message createMessage(Message message, Connection connection) {
        String query = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        ResultSet generatedKeys = null;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, message.getPosted_by());
            statement.setString(2, message.getMessage_text());
            statement.setLong(3, message.getTime_posted_epoch());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                message.setMessage_id(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating message failed, no ID obtained.");
            }
        } catch (SQLException e) {
            // Log exception and throw custom exception
        } finally {
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    // Log exception
                }
            }
        }
        return message;
    }

    public boolean userExists(int user_id, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM Account WHERE account_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user_id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }

        return false;
    }

    public List<Message> getAllMessages(Connection connection) throws SQLException {
        String query = "SELECT * FROM Message ORDER BY time_posted_epoch DESC";
        List<Message> messages = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Message message = new Message();
                message.setMessage_id(resultSet.getInt("message_id"));
                message.setPosted_by(resultSet.getInt("posted_by"));
                message.setMessage_text(resultSet.getString("message_text"));
                message.setTime_posted_epoch(resultSet.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }
        return messages;
    }

    public Message getMessageById(int message_id, Connection connection) throws SQLException {
        String query = "SELECT * FROM Message WHERE message_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, message_id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Message message = new Message();
                    message.setMessage_id(resultSet.getInt("message_id"));
                    message.setPosted_by(resultSet.getInt("posted_by"));
                    message.setMessage_text(resultSet.getString("message_text"));
                    message.setTime_posted_epoch(resultSet.getLong("time_posted_epoch"));
                    return message;
                } else {
                    return null;
                }
            }
        }
    }

    public Message deleteMessageById(int message_id, Connection connection) throws SQLException {
        Message messageToDelete = getMessageById(message_id, connection);
        if (messageToDelete != null) {
            String query = "DELETE FROM Message WHERE message_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, message_id);
                statement.executeUpdate();
            }
        }

        return messageToDelete;
    }

    public List<Message> getAllMessagesForUser(int userId, Connection connection) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM Message WHERE posted_by = ? ORDER BY time_posted_epoch DESC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message(
                            rs.getInt("message_id"),
                            rs.getInt("posted_by"),
                            rs.getString("message_text"),
                            rs.getLong("time_posted_epoch"));
                    messages.add(message);
                }
            }
        }

        return messages;
    }

    public Message updateMessage(Message message, Connection connection) throws SQLException {
        String query = "UPDATE Message SET message_text = ?, time_posted_epoch = ? WHERE message_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, message.getMessage_text());
            statement.setLong(2, message.getTime_posted_epoch());
            statement.setInt(3, message.getMessage_id());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating message failed, no rows affected.");
            }
        }

        return getMessageById(message.getMessage_id(), connection);
    }
}
