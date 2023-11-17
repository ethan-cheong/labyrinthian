package byow.Core;

import java.awt.*;

/**
 * The outcome enum represents several possible outcomes of the game, as well as the messages
 * displayed.
 */
public enum Outcome {
    KILLED_BY_CRAWLER (Color.red, "You were killed by a crawler..."),
    DREAD (Color.blue, "You gave up on escaping..."),
    WON_GAME (Color.yellow, "You escaped the maze!"),
    NOT_VALID_MOVE (Color.black, "How did you see this message?"),
    MOVE (Color.black, "I love cs61bl");

    Outcome(Color messageColor, String message) {
        MESSAGE_COLOR = messageColor;
        MESSAGE = message;
    }
    public final Color MESSAGE_COLOR;
    public final String MESSAGE;
}
