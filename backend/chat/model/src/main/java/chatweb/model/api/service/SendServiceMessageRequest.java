package chatweb.model.api.service;

import chatweb.model.message.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendServiceMessageRequest {
    private Integer userId;
    private String message;
    private List<Button> buttons;
}
