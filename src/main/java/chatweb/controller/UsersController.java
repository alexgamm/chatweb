package chatweb.controller;

import chatweb.model.UserListResponse;
import chatweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserRepository userRepository;

    @GetMapping
    public UserListResponse users() {
        List<UserListResponse.User> list = userRepository.getAllUsers().stream()
                .map(user -> new UserListResponse.User(user.getUsername(), user.getLastActivityAt()))
                .toList();
        return new UserListResponse(list);
    }
}
