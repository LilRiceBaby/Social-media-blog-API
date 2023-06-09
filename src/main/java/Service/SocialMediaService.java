package Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.SocialMediaDAO;
import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

public class SocialMediaService {
    private SocialMediaDAO socialMediaDAO;

    // Inner Exception class for UserAlreadyExistsException
    public static class UserAlreadyExistsException extends Exception {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public SocialMediaService(SocialMediaDAO socialMediaDAO) {
        this.socialMediaDAO = socialMediaDAO;
    }

    public Account createAccount(Connection connection, Account account)
            throws SQLException, UserAlreadyExistsException {
        // Validate username and password
        if (account.getUsername() == null || account.getUsername().isEmpty() || account.getPassword().length() < 4) {
            throw new SQLException("Invalid username or password");
        }

        if (socialMediaDAO.accountExists(account.getUsername(), connection)) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        return socialMediaDAO.createAccount(account, connection);
    }

    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    public Account login(String username, String password) throws SQLException, AuthenticationException {
        Account account = socialMediaDAO.getAccountByUsernameAndPassword(ConnectionUtil.getConnection(), username,
                password);
        if (account == null) {
            throw new AuthenticationException("Invalid username or password.");
        }
        return account;
    }

    public Message createMessage(Message message) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        if (message.getMessage_text() == null || message.getMessage_text().isBlank()) {
            throw new SQLException("Message text cannot be blank");
        }

        if (message.getMessage_text().length() > 254) {
            throw new SQLException("Message text cannot be longer than 254 characters");
        }

        if (!socialMediaDAO.userExists(message.getPosted_by(), connection)) {
            throw new SQLException("User does not exist");
        }
        return socialMediaDAO.createMessage(message, connection);
    }

    public List<Message> retrieveAllMessages() throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        return socialMediaDAO.getAllMessages(connection);
    }

    public Message getMessageById(int message_id) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        return socialMediaDAO.getMessageById(message_id, connection);
    }

    public Message deleteMessageById(int message_id) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        return socialMediaDAO.deleteMessageById(message_id, connection);
    }

    public static class ResourceNotFoundException extends Exception {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public List<Message> getAllMessagesForUser(int userId) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        return socialMediaDAO.getAllMessagesForUser(userId, connection);
    }

    public Message updateMessage(Message message) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();

        if (message.getMessage_text() == null || message.getMessage_text().isBlank()) {
            throw new SQLException("Message text cannot be blank");
        }

        if (message.getMessage_text().length() > 254) {
            throw new SQLException("Message text cannot be longer than 254 characters");
        }

        if (!socialMediaDAO.userExists(message.getPosted_by(), connection)) {
            throw new SQLException("User does not exist");
        }

        return socialMediaDAO.updateMessage(message, connection);
    }
}
