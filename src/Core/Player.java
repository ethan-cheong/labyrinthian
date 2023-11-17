package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;
import java.util.HashMap;


/**
 * Describes the player character. The player character is represented in the world by an AVATAR
 * Tile.
 */
public class Player implements Serializable {
    /* x and y coordinate of the player character */
    private int _x;
    private int _y;

    /* Denotes the range of the player's vision */
    private int _fov;

    /* Player's inventory, mapped to the order they obtained items */
    private HashMap<Integer, Item> inventory;

    /* darkness mechanic */
    private int _dread;
    private int _dreadDecay;

    /* the currently equipped Item */
    private Item equipped;

    private boolean _alive;


    public Player(int x, int y, int dread, int dreadDecay, int visionRange) {
        _x = x;
        _y = y;
        _dread = dread;
        _dreadDecay = dreadDecay;
        _fov = visionRange;
        _alive = true;
    }

    public void decrementDread() {
        _dread -= _dreadDecay;
    }

    public boolean getAlive() {
        return _alive;
    }

    public void makeNotAlive() {
        _alive = false;
    }

    /**
     * Returns the player's FOV.
     */
    public int getFOV() {
        return _fov;
    }

    /**
     * Changes the player's FOV. This happens under certain conditions (e.g. player gets items, etc)
     * @param newFOV the player's new FOV. Should be a positive integer.
     */
    public void setFOV(int newFOV) {
        _fov = newFOV;
    }

    /**
     * Returns the player's x coordinate.
     */
    public int getX(){
        return _x;
    }

    /**
     * Returns the player's y coordinate.
     */
    public int getY(){
        return _y;
    }

    public int getDread(){
        return _dread;
    }

    /**
     * Check if a character's move is valid.
     * @param move one of "wasdWASD"
     * @return true if move is valid (not a wall)
     */
    public boolean checkValidMove(char move, TETile[][] world) {
        switch (move) {
            case 'w', 'W':
                return !(world[_x][(_y+1)].character()=='#');
            case 's', 'S':
                return !(world[_x][(_y-1)].character()=='#');
            case 'a', 'A':
                return !(world[_x-1][_y].character()=='#');
            case 'd', 'D':
                return !(world[_x+1][_y].character()=='#');
            default:
                return false;
        }
    }

    /**
     * Moves around the world.
     * @param move one of "WASDwasd"
     */
    public Direction movePlayer(char move) {
        switch (move) {
            case 'w', 'W':
                _y++;
                return Direction.NORTH;
            case 's', 'S':
                _y--;
                return Direction.SOUTH;
            case 'a', 'A':
                _x--;
                return Direction.WEST;
            case 'd', 'D':
                _x++;
                return Direction.EAST;
            default:
                return null;
        }
    }
}
