package chatweb.model.game;

import lombok.Getter;

@Getter
public enum BoardSize {
    FIVE_X_FIVE(25),
    SIX_X_SIX(36);

    private final int wordAmount;

    BoardSize(int wordAmount) {
        this.wordAmount = wordAmount;
    }
}
