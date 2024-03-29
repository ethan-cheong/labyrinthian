package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * An abstract representation of an Enemy in the Labyrinthian game.
 */
public abstract class Enemy implements Serializable {
    /* Whether the enemy is alive */
    boolean _alive;
    /* Whether the player has aggravated the enemy. Aggravated enemies will behave differently */
    boolean _aggro;

    /* The range of vision for the enemy. */
    int _range;

    /* How many steps the enemy takes per tick */
    int _speed;

    /* current coordinates of the enemy */
    int _x;
    int _y;

    /* Each enemy maintains an internal representation of the world, with its own path filled
    in. */
    TETile[][] _path;

    /* A Linked List of directions describing the path it's taking; passed to the Game for path
    rendering. _plan is emptied every single time the enemy decides to move (Changes every
    turn) */
    LinkedList<Direction> _plan;


    public LinkedList<Direction> getPlan() {
        return _plan;
    }

    /**
     * Returns if the enemy is alive.
     * @return True iff the enemy is alive.
     */
    public boolean isAlive() {
        return _alive;
    }

    /**
     * Returns if the player is in aggro range of the enemy.
     * Range is calculated using the taxicab metric as an distance metric.
     *
     * @param player The player character.
     * @return True iff the player is within the enemy's range
     */
    public boolean inRange(Player player) {
        // use the diamond to see whether enemies aggravated. (taxicab metric)
        int player_y = player.getY();
        int player_x = player.getX();
        return Math.abs(_x - player_x) + Math.abs(_y - player_y) <= _range;
    }

    public int getX() {
        return _x;
    }

    public int getY(){
        return _y;
    }

    /**
     * Moves the enemy. Enemy may move relative to the player's position depending on its state.
     * Returns a cardinal direction to pass to the Game so that the game can modify its TETile[][]
     * world.
     * @param player The player character.
     * @return The direction the enemy has decided to move in this turn.
     */
    public abstract Direction updatePosition(Player player, TETile[][] world);

    /**
     * Returns true iff the enemy is next to the player.
     * @param player
     * @return
     */
    public boolean nextTo(Player player) {
        return (Math.abs(_x - player.getX()) <= 1 && Math.abs(_y - player.getY()) <= 1);
    }
}
