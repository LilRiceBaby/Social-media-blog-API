# Project: Social media blog API

## Background 

When building a full-stack application, we're typically concerned with both a front end, that displays information to the user and takes in input, and a backend, that manages persisted information.

This project will be a backend for a hypothetical social media app, where we must manage our users’ accounts as well as any messages that they submit to the application. The application will function as a micro-blogging or messaging app. In our hypothetical application, any user should be able to see all of the messages posted to the site, or they can see the messages posted by a particular user. In either case, we require a backend which is able to deliver the data needed to display this information as well as process actions like logins, registrations, message creations, message updates, and message deletions.


📌 Features & Requirements

1. Register a New User
   
  -Endpoint: POST /register
  
  -Users can create an account by sending a JSON payload with username and password (no account_id).
  
  -Registration succeeds if:
  
  -Username isn’t blank
  
  -Password is at least 4 characters
  
  -Username doesn’t already exist
  
  -Returns a JSON object with account_id and 200 OK, or 400 Bad Request on failure.

3. User Login
   
  -Endpoint: POST /login
  
  -Users can log in by sending their credentials (without account_id).
  
  -Login succeeds if:
  
  -Credentials match an account in the DB
  
  -Returns the full user object with account_id and 200 OK, or 401 Unauthorized on failure.
   
5. Post a New Message
   
  -Endpoint: POST /messages
  
  -Accepts a JSON payload with posted_by, message_text, and time_posted_epoch (no message_id).
  
  -Success conditions:
  
  -Message isn’t blank
  
  -Less than 255 characters
  
  -posted_by refers to a real user
  
  -Returns the new message including message_id and 200 OK, or 400 Bad Request on failure.
   
7. Get All Messages
   
  -Endpoint: GET /messages
  
  -Returns a list of all messages from the DB.
  
  -Always returns 200 OK. If no messages exist, it returns an empty list.
   
9. Get a Message by ID
    
  -Endpoint: GET /messages/{message_id}
  
  -Fetches a single message by ID.
  
  -Always returns 200 OK, with the message if found or empty response if not.

11. Delete a Message by ID
    
  -Endpoint: DELETE /messages/{message_id}
  
  -Deletes a message with the given ID.
  
  -If it existed, returns the deleted message. If not, returns an empty body.
  
  -Always returns 200 OK to maintain idempotency.

13. Update a Message by ID
    
  -Endpoint: PATCH /messages/{message_id}
  
  -Accepts a new message_text in the body to update the specified message.
  
  -Success conditions:
  
  -Message exists
  
  -message_text is not blank and under 255 characters
  
  -On success, returns updated message and 200 OK. Otherwise, returns 400 Bad Request.

15. Get All Messages by a Specific User
    
  -Endpoint: GET /accounts/{account_id}/messages
  
  -Returns all messages posted by a given user.
  
  -Always returns 200 OK, even if the list is empty.

🧰 Tech Stack
Java + Spring Boot (or insert your backend tech here)
SQL for data persistence
Postman for testing
RESTful API principles

💬 Final Thoughts
This project taught me a lot about backend architecture, handling user input securely, and building out complete CRUD functionality. It’s modular, clean, and easy to build on top of—whether it’s connecting to a frontend or integrating token-based authentication down the line.

If you’re curious to check it out or want to contribute, feel free to reach out!


