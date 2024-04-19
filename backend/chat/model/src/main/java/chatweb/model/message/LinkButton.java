package chatweb.model.message;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LinkButton extends Button {
    private String link;

    public LinkButton(String text, String link) {
        super(ButtonType.LINK, text);
        this.link = link;
    }
}
