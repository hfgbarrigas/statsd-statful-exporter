package co.hold.exceptions;

public class UnknownMetricTypeException extends RuntimeException {
    public UnknownMetricTypeException(String message) {
        super(message);
    }
}
