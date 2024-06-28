package Server.exceptions;

public class InvalidPubKeyException extends Exception {
    public InvalidPubKeyException() {
        super("Invalid given public key");
    }
}
