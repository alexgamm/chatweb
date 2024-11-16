package chatweb.service;

import chatweb.entity.User;
import chatweb.model.Color;
import chatweb.repository.UserRepository;
import chatweb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User createUser(String username, String email) {
        User user = new User()
                .setEmail(email.toLowerCase())
                .setUsername(username.toLowerCase());
        return userRepository.save(user);
    }

    public User createUser(String username, String email, String password) {
        User user = new User()
                .setEmail(email.toLowerCase())
                .setUsername(username.toLowerCase())
                .setPassword(PasswordUtils.hash(password));
        return userRepository.save(user);
    }

    @Nullable
    public User findUserByEmail(@Nullable String email) {
        return Optional.ofNullable(email)
                .map(String::toLowerCase)
                .map(userRepository::findUserByEmail)
                .orElse(null);
    }

    public boolean existsByEmail(@NotNull String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    public boolean existsByUsername(@NotNull String username) {
        return userRepository.existsByUsername(username.toLowerCase());
    }

    @Transactional
    public void updatePassword(int userId, @NotNull String password) {
        userRepository.updatePassword(userId, PasswordUtils.hash(password));
    }

    @Transactional
    public void updateUsername(int userId, String username) {
        userRepository.updateUsername(userId, username.toLowerCase());
    }

    @Transactional
    public void updateColor(int userId, Color color) {
        userRepository.updateColor(userId, color);
    }

}
