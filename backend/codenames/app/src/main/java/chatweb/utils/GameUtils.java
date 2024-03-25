package chatweb.utils;

import chatweb.model.game.state.Card;
import chatweb.model.game.state.CardType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GameUtils {

    public static final int MIN_TEAM_CARD_AMOUNT = 8;

    public static List<Card> createCards(LinkedList<String> shuffledWords, List<Integer> shuffledTeamIds) {
        // на 1 слово больше у стартующей команды
        // 7 белых слов в игре 5х5 + 1 черное + 8,9 слов у 2х команд,
        // если игра 6х6, то у команд 10, 9, 8 слов + 8 нейтальных
        List<Card> cards = new ArrayList<>();
        // adding color cards for teams
        for (int teamIdx = 0; teamIdx < shuffledTeamIds.size(); teamIdx++) {
            Integer teamId = shuffledTeamIds.get(teamIdx);
            for (int i = 0; i < MIN_TEAM_CARD_AMOUNT + teamIdx; i++) {
                cards.add(new Card(CardType.COLOR, shuffledWords.pop(), teamId, null));
            }
        }
        // adding black word
        cards.add(new Card(CardType.BLACK, shuffledWords.pop(), null, null));
        // adding neutral word
        for (String word : shuffledWords) {
            cards.add(new Card(CardType.NEUTRAL, word, null, null));
        }
        Collections.shuffle(cards);
        return cards;
    }
}
