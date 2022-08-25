package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import static byow.TileEngine.Tileset.*;

/**
 *  Class that describes the state of a Game.
 */
public class Game implements Serializable {

    Player _player;                      /* player character */
    TETile[][] _world;              /* world with paths filled in*/
    TETile[][] _worldNoPaths;       /* clean copy of the world; passed to enemies */
    boolean _dark;                  /* indicates whether the lights are on or off */
    ArrayList<Enemy> _enemies = new ArrayList<>();      /* enemies */
    ArrayList<Item> _items;         /* items */
    boolean _running;               /* is the game not over */
    int _ticks;                     /* How many turns have passed */
    Difficulty _difficulty;

    int _exitX;
    int _exitY;

    Random _random;

    // TODO: define default Crawler parameters for instantiation

    public Game(TETile[][] world, Random random, Difficulty difficulty) {
        _world = world;
        _worldNoPaths = TETile.copyOf(world);
        _random = random;
        _dark = true;
        _difficulty = difficulty;

        // parse the TETile array
        for (int i = 0; i < _world.length; i++) {
            for (int j = 0; j < _world[0].length; j++ ) {
                // parse each character
                TETile currentTile = _world[i][j];
                if (AVATAR.equals(currentTile)) { // player avatar
                    _player = new Player(i, j, _difficulty.DREAD, _difficulty.DREAD_DECAY,
                            _difficulty.VISION_RANGE);
                } else if (currentTile.equals(CRAWLER)) {
                    _enemies.add(new Crawler(i, j, _difficulty.CRAWLER_RANGE,
                            _difficulty.CRAWLER_SPEED,
                            random, world));
                } else if (currentTile.equals(EXIT)) {
                    _exitX = i;
                    _exitY = j;
                    System.out.println(_exitX + ", " + _exitY);
                }
                //TODO: parse other enemies and items.
            }
        }
        _running = true;
    }

    /**
     * Returns true if the game is still running.
     * @return true iff the game is still running.
     */
    public boolean isRunning() {
        return _running;
    }


    /**
     * Increments time in-game. Time only passes when the player character makes a valid move.
     * Returns the outcome of the turn.
     */
    public Outcome tickTime(char move) {


        if (!_player.checkValidMove(move, _world)) {
            return Outcome.NOT_VALID_MOVE;
        }


        // erase all paths from the world
        _world = TETile.copyOf(_worldNoPaths);

        updatePlayerPosition(move);
        _player.decrementDread();
        // for each active enemy:
        //  update their aggro state
        //  update their positions depending on if they're aggro or just moving around.
        //  idea: they handle their own movement in the class, pass a movement signal
        //  to the Game, game uses it to update it's own TETile array?

        updateEnemyPositions();
        // Update all enemies in enemies.
        // Update items.
        // Update world based on new positions of player, characters, items.
        // Update gamestate if no longer running
        _ticks ++;

        // check if the player has won the game
        System.out.println(_player.getX() + ", " + _player.getY());
        if (_player.getX() == _exitX && _player.getY() == _exitY) {
            _running = false;
            return Outcome.WON_GAME;
        }

        // check if the player is still alive
        if (_player.getDread() <= 0) {
            _running = false;
            return Outcome.DREAD;
        }

        for (Enemy e: _enemies) {
            if (e.nextTo(_player)) {
                _player.makeNotAlive();
                _running = false;
                return Outcome.KILLED_BY_CRAWLER;
            }
        }



        return Outcome.MOVE;
    }

    /**
     * Update the world array depending on the player's move.
     * @param move one of "WASDwasd"
     */
    private void updatePlayerPosition(char move) {
        int x = _player.getX();
        int y = _player.getY();
        int newX = x;
        int newY = y;
        Direction direction = _player.movePlayer(move);
        switch (direction) {
            case NORTH:
                newY ++;
                break;
            case SOUTH:
                newY--;
                break;
            case WEST:
                newX--;
                break;
            case EAST:
                newX++;
                break;
        }
        // make this floor for now.
        _world[x][y] = _world[newX][newY].copyOfNotPath();
        _worldNoPaths[x][y] = _worldNoPaths[newX][newY].copyOfNotPath();
        _world[newX][newY] = AVATAR;
        _worldNoPaths[newX][newY] = AVATAR;
    }

    /**
     * Update enemies' positions in the world array.
     */
    private void updateEnemyPositions() {
        TETile enemyTile = null;
        for (Enemy e : _enemies) {
            if (e instanceof Crawler) {
                enemyTile = CRAWLER;
            }

            if (!_dark) {
                enemyTile = enemyTile.copyOfDarkFlipped();
            }

            //_world = e.getPath();
            int x = e.getX();
            int y = e.getY();
            int newX = x;
            int newY = y;
            Direction direction = e.updatePosition(_player, _worldNoPaths);

            // update the world with the enemy's path.
            updateEnemyPath(e, _world);

            // update the _world and _worldNoPath arrays without keeping paths.
            switch (direction) {
                case NORTH:
                    newY++;
                    break;
                case SOUTH:
                    newY--;
                    break;
                case WEST:
                    newX--;
                    break;
                case EAST:
                    newX++;
                    break;
                case NOPE:
                    break;
            }
            _world[x][y] = _world[newX][newY].copyOfNotPath();
            _worldNoPaths[x][y] = _worldNoPaths[newX][newY].copyOfNotPath();
            _world[newX][newY] = enemyTile;
            _worldNoPaths[newX][newY] = enemyTile;
        }
    }
    // TODO: modify world directly, so we don't have to copy it
    /**
     * Given an enemy e, fill in the world with its path.
     * @param e
     * @return TETile[][]
     */
    private void updateEnemyPath(Enemy e, TETile[][] world) {
        LinkedList<Direction> plan = e.getPlan();
        int x = e.getX();
        int y = e.getY();
        for (Direction currentDirection: plan) {
            switch (currentDirection) {
                case NORTH:
                    // change the pointers
                    y++;
                    break;
                case SOUTH:
                    y--;
                    break;
                case EAST:
                    x++;
                    break;
                case WEST:
                    x--;
                    break;
                case NOPE:
                    break;
            }
            world[x][y] = world[x][y].copyOfPath();
        }
    }

    /**
     * Changes world from dark to light.
     */
    public void toggleDark() {
        _dark = !_dark;

        for (int i = 0; i < _world.length; i++) {
            for (int j = 0; j < _world[0].length; j++ ) {
                if (_world[i][j].character() == '@') { // player avatar
                    continue;
                }
                _world[i][j] = _world[i][j].copyOfDarkFlipped();
                _worldNoPaths[i][j] = _worldNoPaths[i][j].copyOfDarkFlipped();
            }
        }
    }

    /**
     * Returns current state of the world / the mask, depending on whether dark is true.
     * @return The current world, as a TETile array.
     */
    public TETile[][] getWorld() {

        if (!_dark) {
            return _world;
        }

        TETile[][] toReturn = TETile.copyOf(_world);

        int radius = _player.getFOV();
        int center_x = _player.getX();
        int center_y = _player.getY();

        // Get a square centred at the player and of side length radius*2+1
        //    x
        //   xxx
        //  xxxxx
        // xxx@xxx
        //  xxxxx
        //   xxx
        //    x
        for (int y = center_y - radius; y < center_y + radius + 1; y++) {
            for (int x = center_x - radius; x < center_x + radius + 1; x++) {
                // flip colour if x and y coords are taxicab metric distance from center
                // open ball for radius r for the taxicab metric is a diamond
                if ((y==center_y && x==center_x) || x<0 || y < 0
                        || x >= _world.length || y >=_world[0].length) {
                    continue;
                }
                if (Math.abs(x - center_x) + Math.abs(y - center_y) <= radius) {
                    toReturn[x][y] = toReturn[x][y].copyOfDarkFlipped();
                }
            }
        }
        return toReturn;
    }


}
