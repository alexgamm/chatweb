package webserver;

public class RequestFailedException extends Exception{
    private final int statusCode;
    private final String description;

    public RequestFailedException(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }
}
