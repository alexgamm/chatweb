package chatweb.endpoint;

import chatweb.model.UserListResponse;
import chatweb.repository.UserRepository;
import webserver.Endpoint;
import webserver.Request;
import webserver.RequestFailedException;

import java.util.List;

public class UsersEndpoint implements Endpoint {
    private final UserRepository userRepository;

    public UsersEndpoint(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Object get(Request request) throws RequestFailedException {
        List<UserListResponse.User> list = userRepository.getAllUsers().stream()
                .map(user -> new UserListResponse.User(user.getUsername(), user.getLastActivityAt()))
                .toList();
        return new UserListResponse(list);
    }
}
