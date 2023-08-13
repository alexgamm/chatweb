package chatweb.exception.telegram;

public class TelegramOAuthException extends Exception {
    public TelegramOAuthException(Throwable cause) {
        super(cause);
    }

    public TelegramOAuthException(String message) {
        super(message);
    }
}
