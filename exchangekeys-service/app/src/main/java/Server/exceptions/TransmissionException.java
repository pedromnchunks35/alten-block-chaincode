package Server.exceptions;

public class TransmissionException extends Exception {
    public TransmissionException(String message) {
        super("Error in the transmission " + message);
    }
}
