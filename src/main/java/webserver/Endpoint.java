package webserver;

public interface Endpoint {
    default Object get(Request request) throws RequestFailedException {
        throw new RequestFailedException(405, "Method not allowed");
    }

    default Object post(Request request) throws RequestFailedException {
        throw new RequestFailedException(405, "Method not allowed");
    }
}
