package byow.Core;

import byow.TileEngine.TETile;
import java.util.*;
import static byow.Core.Direction.*;

/**
 * Class representing a crawler.
 * Crawlers wander through the map randomly, until the player steps into their range, which
 * aggravates them. Distance is measured using the taxicab metric.
 * Once the player steps into their range, they calculate the shortest path to the player using
 * Dijkstra's algorithm.
 */
public class Crawler extends Enemy {
    Random _random;

    public Crawler(int x, int y, int range, int speed, Random random, TETile[][] world) {
        _alive = true;
        _aggro = false;
        _range = range;
        _speed = speed;
        _x = x;
        _y = y;
        _random = random;
        _path = world;
        _plan = new LinkedList<>();
    }

    /**
     * Checks if a crawler's move is valid.
     * @param move the direction that is being checked.
     * @param world the current representation of the Labyrinthian world.
     * @return true iff the move is valid given the representation of the world.
     */
    private boolean isValidMove(Direction move, TETile[][] world){
        switch (move) {
            case NORTH:
                return (world[_x][(_y+1)].character()!='#' && world[_x][(_y+1)].character()!='░'
                        && world[_x][_y+1].character() != '*');
            case SOUTH:
                return (world[_x][(_y-1)].character()!='#' && world[_x][(_y-1)].character()!='░' &&
                        world[_x][_y-1].character() != '*');
            case EAST:
                return (world[_x+1][_y].character()!='#' && world[_x+1][_y].character()!='░'  &&
                        world[_x+1][_y].character() != '*');
            case WEST:
                return (world[_x-1][_y].character()!='#' && world[_x-1][_y].character()!='░' &&
                        world[_x-1][_y].character() != '*');
            default:
                return false;
        }
    }

    /** Updates the enemy's path depending on whether they are aggro'd or not.
     * If aggro, crawler uses dijkstra's to find the shortest path to the player given the
     * TETile[][]
     * array provided. Otherwise it decides to move pseudo-randomly out of all the possible moves
     * it can make.
     * @param player
     * @param world  this should be a CLEAN world with no paths filled in.
     */
    private void decidePath(Player player, TETile[][] world) {
        _plan.clear();

        if (inRange(player)) {
            _aggro = true;
        } else {
            _aggro = false;
        }
        // starts chasing the player
        if (_aggro) {
            _path = dijkstra(world, player.getX(), player.getY());
        }
        // Move randomly if not aggro'd
        if (!_aggro) {
            Direction[] validMoves = new Direction[5];
            //NORTH SOUTH NOPE NULL NULL
            int i=0;
            for (Direction d: new Direction[]{NORTH, SOUTH, EAST, WEST, NOPE} ) {
                if (d.equals(NOPE)) {
                    validMoves[i] = d;
                    i++;
                } else if (isValidMove(d, world)) {
                    validMoves[i] = d;
                    i++;
                }
            }
            RandomUtils.shuffle(_random, validMoves, 0, i);
            Direction direction = validMoves[0];

            switch (direction){
                case NORTH:
                    if (isValidMove(NORTH, world)) {
                        _path[_x][_y+1] = _path[_x][_y+1].copyOfPath();
                        _plan.add(NORTH);
                    }
                    break;
                case SOUTH:
                    if (isValidMove(SOUTH, world)) {
                        _path[_x][_y-1] = _path[_x][_y-1].copyOfPath();
                        _plan.add(SOUTH);
                    }
                    break;
                case WEST:
                    if (isValidMove(WEST, world)) {
                        _path[_x-1][_y] = _path[_x-1][_y].copyOfPath();
                        _plan.add(WEST);
                    }
                    break;
                case EAST:
                    if (isValidMove(EAST, world)) {
                        _path[_x+1][_y] = _path[_x+1][_y].copyOfPath();
                        _plan.add(EAST);
                    }
                    break;
            }
        }
    }

    /**
     * Looks at paths around it and decides if it should follow it. Also decides on its next path
     * after taking the action. Returns the direction it takes.
     * @param player the player character
     * @return Direction that the enemy has decided to move in this turn.
     */
    public Direction updatePosition(Player player, TETile[][] world) {
        if (_path[_x+1][_y].isPath() && world[_x+1][_y].character() != '░') {
            _path[_x+1][_y] = _path[_x+1][_y].copyOfNotPath();
            _x++;
            decidePath(player, world);
            return EAST;
        }
        if (_path[_x-1][_y].isPath() && world[_x-1][_y].character() != '░') {
            _path[_x-1][_y] = _path[_x-1][_y].copyOfNotPath();
            _x--;
            decidePath(player, world);
            return WEST;
        }
        if (_path[_x][_y+1].isPath() && world[_x][_y+1].character() != '░') {
            _path[_x][_y+1] = _path[_x][_y+1].copyOfNotPath();
            _y++;
            decidePath(player, world);
            return NORTH;
        }
        if (_path[_x][_y-1].isPath() && world[_x][_y-1].character() != '░') {
            _path[_x][_y-1] = _path[_x][_y-1].copyOfNotPath();
            _y--;
            decidePath(player, world);
            return SOUTH;
        }
        decidePath(player, world);
        return NOPE;
    }

    /**
     * Helper for updatePosition. Crawler uses Dijkstra's on its own x and y coordinates, and the
     * x and y coordinates of the player (dest_x, dest_y). Its own x and y should not be modified.
     * BFS uses matrix coordinates internally.
     * @param dest_x
     * @param dest_y
     * @return TETile array with the path highlighted (floor changed to PATH Tiles).
     */
    private TETile[][] dijkstra(TETile[][] world,  int dest_x, int dest_y) {

        /**
         * Class that represents a coordinate.
         */
        class PriorityCoordinate implements Comparable<PriorityCoordinate> {
            int _x;
            int _y;
            int _priority;

            public PriorityCoordinate(int x, int y, int priority) {
                _x = x;
                _y = y;
                _priority = priority;
            }

            @Override
            public int compareTo(PriorityCoordinate other) {
                return this._priority - other._priority;
            }

            @Override
            public boolean equals(Object o) {
                if (o==null) {
                    return false;
                }
                if (this == o) {
                    return true;
                }
                if (o instanceof PriorityCoordinate) {
                    PriorityCoordinate other = (PriorityCoordinate) o;
                    return (_x == other._x) && (this._y == other._y);
                }
                return false;
            }

            @Override
            public String toString() {
                return "["+_x+","+_y+"]";
            }

            public static void printArray(PriorityCoordinate[][] input) {
                for (int i=0; i<input.length; i++) {
                    for (int j=0; j<input[0].length; j++) {
                        if (input[i][j]==null) {
                            System.out.print(i+","+j+"[-,-]");
                        } else {
                            System.out.print(i+","+j+input[i][j]);
                        }
                    }
                    System.out.println();
                }
            }
        }

        int INF = Integer.MAX_VALUE;
        PriorityCoordinate[][] prevTile = new PriorityCoordinate[world.length][world[0].length];
        int[][] distances = new int[world.length][world[0].length];
        boolean[][] visited = new boolean[world.length][world[0].length];

        //these help get the neighbors: let E be the enemy/starting vertex, O represents the neighbors
        // # # # # #
        // # O # # #
        // O E O # #
        // # O # # #
        // # # # # #
        int[] rowScale = new int[]{-1, 0, 1, 0};
        int[] colScale = new int[]{0, 1, 0, -1};

        PriorityQueue<PriorityCoordinate> fringe = new PriorityQueue<>();
        // initialize values for fringe and distances.
        for (int i=0; i<world.length; i++) {
            for (int j=0; j<world[0].length; j++) {
                if (!(world[i][j].character()=='#')) {
                    distances[i][j] = INF;
                    fringe.add(new PriorityCoordinate(i, j, INF));
                }
            }
        }

        // update priority value and prevtile of the start
        prevTile[_x][_y] = new PriorityCoordinate(_x, _y, 0);
        distances[_x][_y] = 0;
        fringe.add(new PriorityCoordinate(_x, _y, 0));

        //make a copy of given world
        TETile[][] bfsPath = TETile.copyOf(world);

        while (!fringe.isEmpty()) {
            PriorityCoordinate current = fringe.poll();
            int current_x = current._x;
            int current_y = current._y;

            if ((current_x == dest_x) && (current_y == dest_y)) {
                break;
            }

            if (visited[current_x][current_y]) {
                continue;
            }

            //after removing, we look at the neighbors
            for (int i = 0; i < 4; i++) {
                //helps get the top, bottom, left, right neighbor
                int adjX = current_x + rowScale[i];
                int adjY = current_y + colScale[i];
                //check if it's not been visited yet
                if (isValid(adjX, adjY, world)) {
                    if (distances[current_x][current_y] + 1 < distances[adjX][adjY]) {
                        distances[adjX][adjY] = distances[current_x][current_y] + 1;
                        prevTile[adjX][adjY] = new PriorityCoordinate(current_x, current_y, 0);
                        fringe.add(new PriorityCoordinate(adjX, adjY, distances[adjX][adjY]));
                    }
                }
            }
            visited[current_x][current_y] = true;
        }

        int pointerX = dest_x;
        int pointerY = dest_y;
        int newPointerX;
        int newPointerY;
        Stack<Direction> stack = new Stack<>(); // used to produce crawler's plan.
        while ((pointerX != _x) || (pointerY != _y)) {
            bfsPath[pointerX][pointerY] =
                    world[pointerX][pointerY].copyOfPath();
            newPointerX = prevTile[pointerX][pointerY]._x;
            newPointerY = prevTile[pointerX][pointerY]._y;
            // do something to get the plan

            if (newPointerX == pointerX+1) {
                stack.push(WEST);
            } else if (newPointerX == pointerX-1) {
                stack.push(EAST);
            } else if (newPointerY == pointerY - 1) {
                stack.push(NORTH);
            } else if (newPointerY == pointerY + 1) {
                stack.push(SOUTH);
            }
            pointerX = newPointerX;
            pointerY = newPointerY;
        }
        while (!stack.empty()) {
            // pop Directions from the stack and put into plan.
            _plan.add(stack.pop());
        }
        return bfsPath;
    }

    /**
     * Helper for Dijkstra's. It checks whether we should consider the tile for Dijkstra's.
     * @param row     indexes the row
     * @param col     indexes the column
     * @return        true iff the tile hasn't already been visited.
     */
    private boolean isValid(int row, int col, TETile[][] world) {
        //if it's out of bounds, return false
        if (row < 0 || row >= world.length || col < 0 || col >= world[0].length) {
            return false;
        }
        // if it's a wall, return false
        if (world[row][col].character() == '#') {
            return false;
        }
        return true;
    }

    public void toggleAggro() {
        _aggro = !_aggro;
    }
}
