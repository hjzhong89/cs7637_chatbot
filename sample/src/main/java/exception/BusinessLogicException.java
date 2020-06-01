package exception;

/**
 * Wrapper for exceptions that occur in Controller and business logic classes
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
public class BusinessLogicException extends Exception {

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String message, Exception e) {
        super(message, e);
    }

}
