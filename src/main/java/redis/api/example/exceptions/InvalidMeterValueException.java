package redis.api.example.exceptions;

public class InvalidMeterValueException extends RuntimeException {
    public InvalidMeterValueException(double meterValue) {
        super("Invalid meter value: " + meterValue + ". The value must be greater than zero.");
    }

    public InvalidMeterValueException(String input) {
        super("Invalid input: '" + input + "'. The value must be a valid number.");
    }
    
}
