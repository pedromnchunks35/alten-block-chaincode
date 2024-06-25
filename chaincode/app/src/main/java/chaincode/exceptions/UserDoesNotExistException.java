package chaincode.exceptions;

public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException() {
        super("The user does not exist");
    }
}
