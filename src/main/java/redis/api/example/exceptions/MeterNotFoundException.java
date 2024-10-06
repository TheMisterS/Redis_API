package redis.api.example.exceptions;

public class MeterNotFoundException extends RuntimeException {
    public MeterNotFoundException() {
        super("Meter not found");
    }
}
