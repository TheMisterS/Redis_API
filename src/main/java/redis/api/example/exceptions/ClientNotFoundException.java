package redis.api.example.exceptions;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(int id)
    {
        super("Client with ID " + id + " not found.");
    }
}
