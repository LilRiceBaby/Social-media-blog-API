package Controller;

import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import DAO.SocialMediaDAO;
import Model.Account;
import Model.Message;
import Service.SocialMediaService;
import Util.ConnectionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class SocialMediaController {
    private SocialMediaService socialMediaService;

    public SocialMediaController() {
        SocialMediaDAO socialMediaDAO = new SocialMediaDAO();
        this.socialMediaService = new SocialMediaService(socialMediaDAO);
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerAccountHandler);
        app.post("/login", this::loginAccountHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::retrieveAllMessagesHandler);
        app.get("/messages/{id}", this::getMessageByIdHandler);
        app.delete("/messages/{id}", this::deleteMessageByIdHandler);
        app.get("/accounts/{userId}/messages", this::getAllMessagesForUserHandler);
        app.patch("/messages/{id}", this::updateMessageHandler);

        return app;
    }

    private void registerAccountHandler(Context ctx) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
            ctx.status(400);
            ctx.result(""); // respond with an empty response body
        } else {
            try {
                Account createdAccount = socialMediaService.createAccount(ConnectionUtil.getConnection(), account);
                ctx.status(200);
                ctx.json(mapper.writeValueAsString(createdAccount));
            } catch (SQLException e) {
                e.printStackTrace();
                ctx.status(400); // if the SQLException is due to invalid username or password
            } catch (SocialMediaService.UserAlreadyExistsException e) {
                e.printStackTrace();
                ctx.status(400); // if the UserAlreadyExistsException is due to a duplicate username
            }
        }
    }

    private void loginAccountHandler(Context ctx) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        try {
            Account loggedInAccount = socialMediaService.login(account.getUsername(), account.getPassword());
            ctx.status(200);
            ctx.json(mapper.writeValueAsString(loggedInAccount));
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.status(500);
        } catch (SocialMediaService.AuthenticationException e) {
            e.printStackTrace();
            ctx.status(401);
        }
    }

    private void createMessageHandler(Context ctx) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message message = mapper.readValue(ctx.body(), Message.class);
            Message createdMessage = socialMediaService.createMessage(message);
            ctx.status(200).json(mapper.writeValueAsString(createdMessage));
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.status(400);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            ctx.status(400);
        }
    }

    private void retrieveAllMessagesHandler(Context ctx) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Message> messages = socialMediaService.retrieveAllMessages();
            ctx.status(200).json(mapper.writeValueAsString(messages));
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.status(500);
        }
    }

    private void getMessageByIdHandler(Context ctx) throws JsonMappingException, JsonProcessingException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        try {
            Message message = socialMediaService.getMessageById(id);
            if (message == null) {
                ctx.status(200).result(""); // Return 200 and an empty body when message is not found
            } else {
                ObjectMapper mapper = new ObjectMapper();
                ctx.status(200).json(mapper.writeValueAsString(message));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.status(500);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            ctx.status(400);
        }
    }

    private void deleteMessageByIdHandler(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        try {
            Message message = socialMediaService.deleteMessageById(id);
            if (message == null) {
                ctx.status(200).result(""); // Return 200 and an empty body when message is not found
            } else {
                ObjectMapper mapper = new ObjectMapper();
                ctx.status(200).json(mapper.writeValueAsString(message)); // Success
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.status(500); // Internal Server Error
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            ctx.status(400); // Bad Request
        }
    }

    private void getAllMessagesForUserHandler(Context ctx) {
        int userId = Integer.parseInt(ctx.pathParam("userId"));

        try {
            List<Message> messages = socialMediaService.getAllMessagesForUser(userId);
            ctx.status(200).json(messages);
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.status(500);
        }
    }

    private void updateMessageHandler(Context ctx) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int id = Integer.parseInt(ctx.pathParam("id"));
        String newText = mapper.readTree(ctx.body()).get("message_text").asText();

        try {
            Message message = socialMediaService.getMessageById(id);
            if (message == null) {
                ctx.status(400); // Not Found
            } else {
                message.setMessage_text(newText);
                Message updatedMessage = socialMediaService.updateMessage(message);
                ctx.status(200).json(mapper.writeValueAsString(updatedMessage));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.status(400);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            ctx.status(400);
        }
    }

}

//this is a new comment
