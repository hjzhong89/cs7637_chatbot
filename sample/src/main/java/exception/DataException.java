package exception;

/**
 * Created by jaimezhong on 11/18/16.
 */
public class DataException extends Exception {
    public DataException(String message, Exception e) {
        super(message, e);
    }
}
