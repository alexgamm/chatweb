package chatweb.service;

import chatweb.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatGPTService {

    public void handleMessage(chatweb.entity.Message message) {
        String mention = "@" + chatGPTProperties.getUsername();
        User user = getOrCreateUser();
        boolean isChatGPTMentioned = message.getMessage().startsWith(mention);
        boolean isReplyToChatGPT = Optional.ofNullable(message.getRepliedMessage())
                .map(chatweb.entity.Message::getUser)
                .map(User::getId)
                .map(id -> id.equals(user.getId()))
                .orElse(false);
        if (!isChatGPTMentioned && !isReplyToChatGPT) {
            return;
        }
        taskScheduler.schedule(() -> {
            try {
                String messageToComplete = isChatGPTMentioned
                        ? message.getMessage().replace(mention, "")
                        : message.getMessage();
                String completionContent = getCompletionContent(messageToComplete);
                chatweb.entity.Message completionMessage = messageRepository.save(new chatweb.entity.Message(
                        UUID.randomUUID().toString(),
                        completionContent,
                        message.getRoom(),
                        user,
                        null,
                        message,
                        new Date(),
                        Collections.emptyList()
                ));
                eventsProducer.addEvent(new NewMessageEvent(
                        MessageMapper.messageToMessageDto(completionMessage, null, false)
                ));
            } catch (ChatCompletionException e) {
                // TODO handle properly
                throw new RuntimeException(e);
            }
        }, Instant.now());
    }

    public User getOrCreateUser() {
        return Optional.ofNullable(userRepository.findUserByUsername(chatGPTProperties.getUsername()))
                .orElseGet(() -> userRepository.save(new User(
                        null,
                        chatGPTProperties.getUsername(),
                        null,
                        null,
                        UserColorUtils.getRandomColor()
                )));
    }
}
