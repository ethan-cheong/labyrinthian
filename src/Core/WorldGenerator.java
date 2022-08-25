package byow.Core;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static byow.TileEngine.Tileset.*;

/**
 * Class that generates a world randomly.
 */
public class WorldGenerator {
    int _height;
    int _width;
    Random _random;
    // Specified minimum room size
    // ######
    // #----#
    // #----#
    // #----#
    // #----#
    // ######
    int MIN_ROOM_DIM = 3;
    // Specified maximum room size
    int MAX_ROOM_DIM = 7;
    double P_CREATE_ROOM = 0.01;
    Difficulty _difficulty;

    public WorldGenerator(int width, int height, Long seed, Difficulty difficulty) {
        _height = height;
        _width = width;
        _random = new Random(seed);
        _difficulty = difficulty;
    }

    public TETile[][] generateWorld() {
        // TODO: add in item, terrain generation
        List<Room> roomList = generateRooms();
        RoomGraph roomGraph = new RoomGraph(roomList, _random);
        roomGraph.pa(); // should convert roomGraph into an MST in-place;
        roomGraph.addRandomEdges(2); // Try adding at least two edges in the graph so we get cycles
        // Node that worldRep is rendered upside-down
        int[][] worldRep = generateHallways(roomGraph);
        TETile[][] world = convertToTiles(worldRep);
        addPlayer(world);
        addEnemies(world, _difficulty.CRAWLER_NUM);
        addExit(world);
        return world;
    }

    /**
     * Adds the player into the world.
     * @return
     */
    private void addPlayer(TETile[][] world) {
        // Just initialize at the top left corner for now lol
        for (int i=0; i<_height; i++) {
            for (int j=0; j<_width; j++) {
                if (world[j][i].equals(FLOOR)) {
                    world[j][i] = AVATAR;
                    return;
                }
            }
        }
    }

    /**
     * Adds the exit into the world. (I'll take this out eventually, not a very interesting win
     * condition)
     */

    private void addExit(TETile[][] world) {
        for (int i=_height-1; i>=0; i--) {
            for (int j=_width-1; j>=0; j--) {
                if (world[j][i].equals(FLOOR)) {
                    if (RandomUtils.bernoulli(_random, 0.3)) {
                        world[j][i] = EXIT;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Adds n enemies into the world.
     * @param n The number of enemies to add.
     * @param world The TETile[][] world to modify in-place.
     */
    private void addEnemies(TETile[][] world, int n) {
        int counter = 0;
        for (int i=_height-1; i>=0; i--) {
            for (int j=_width-1; j>=0; j--) {
                if (world[j][i].equals(FLOOR) && counter < n) {
                    if (RandomUtils.bernoulli(_random, 0.005)) {
                        world[j][i] = CRAWLER;
                        counter++;
                    }
                }
                if (counter==n) {
                    return;
                }
            }
        }
    }

    /**
     * Return a roomList containing randomly generated rooms.
     *
     * @return roomList
     */
    private List<Room> generateRooms() {
        List<Room> result = new ArrayList<>();
        generateRoomsHelper(_height, _width, 0, 0, result);
        return result;
    }

    /**
     * Recursive helper for the generateRooms method.
     *
     * @param parentHeight Height of the parent.
     * @param parentWidth  Width of the parent.
     * @param parentX      x coordinate (lower left corner) of parent.
     * @param parentY      y coordinate (lower left corner) of parent.
     * @param roomList     roomList to append Rooms to.
     */
    private void generateRoomsHelper(int parentHeight, int parentWidth, int parentX, int parentY, List<Room> roomList) {
        // Base case: if parent is too small, do nothing; rooms can't fit inside.
        if (parentHeight < MIN_ROOM_DIM + 2 || parentWidth < MIN_ROOM_DIM + 2) {
            return;
        } else if ((parentHeight >= MIN_ROOM_DIM + 2 && parentHeight < MAX_ROOM_DIM + 2) &&
                (parentWidth >= MIN_ROOM_DIM + 2 && parentWidth < MAX_ROOM_DIM + 2)) {
            // If width and height are in good range, we can make a room.
            boolean createRoom = RandomUtils.bernoulli(_random, P_CREATE_ROOM);
            if (createRoom) {
                // create a room. [3,6) -> [3,5]

                int roomHeight = RandomUtils.uniform(_random, MIN_ROOM_DIM, parentHeight-1);
                int roomWidth = RandomUtils.uniform(_random, MIN_ROOM_DIM, parentWidth-1);
                int roomX = RandomUtils.uniform(_random, parentX, parentWidth-2-roomWidth+parentX+1);
                int roomY = RandomUtils.uniform(_random, parentY, parentHeight-2-roomHeight+parentY+1);
                roomList.add(new Room(roomHeight, roomWidth, roomX, roomY));
            } else {
                // do nothing for now.
                // TODO: try recursing further if too sparse
            }
        } else {
            if (parentHeight >= MAX_ROOM_DIM + 2) {
                // if too tall, do a horizontal split.
                //TODO: maybe change this topHeight randomization if boxes are too thin
                // pick split height

                double splitRatio = RandomUtils.uniform(_random, 0.5, 0.7);
                int topHeight = ((Double) (parentHeight * splitRatio)).intValue();
                int bottomHeight = parentHeight - topHeight;
                int bottomX = parentX;
                int bottomY = parentY;
                int topX = parentX;
                int topY = parentY + bottomHeight;
                // recurse on top box
                generateRoomsHelper(topHeight, parentWidth, topX, topY, roomList);
                // recurse on bottom box
                generateRoomsHelper(bottomHeight, parentWidth, bottomX, bottomY, roomList);
            }
            if (parentWidth >= MAX_ROOM_DIM + 2) {
                // do a vertical split
                // pick split width
                double splitRatio = RandomUtils.uniform(_random, 0.5, 0.7);
                int rightWidth = ((Double) (parentWidth * splitRatio)).intValue();
                int leftWidth = parentWidth - rightWidth;
                int leftX = parentX;
                int leftY = parentY;
                int rightX = leftWidth+parentX;
                int rightY = parentY;
                // recurse on left box
                generateRoomsHelper(parentHeight, leftWidth, leftX, leftY, roomList);
                // recurse on right box
                generateRoomsHelper(parentHeight, rightWidth, rightX, rightY, roomList);
            }
        }
    }

    /**
     * Given a roomGraph, generate hallways between room centers,
     * then overlay onto an int array representation
     */
    private int[][] generateHallways(RoomGraph roomGraph) {
        // worldRep represents the rooms in the world
        int[][] worldRep = emptyOutRooms(roomGraph);
        double[][] edges = roomGraph.getAdjMatrix();
        List<Room> roomList = roomGraph.getRoomList();
        // for each edge in the adjacency matrix, carve out a hallway.
        for (int i = 0; i < edges.length; i++) {
            for (int j = 0; j < edges.length; j++) { // matrix is always square
                if (edges[i][j] != 0.0) { // an edge exists

                    // get the rooms to draw a path between
                    Room startRoom = roomList.get(i);
                    int[] startCoords = startRoom.getCenter();
                    Room endRoom = roomList.get(j);
                    int[] endCoords = endRoom.getCenter();

                    if (Math.abs(startCoords[0] - endCoords[0]) <= endRoom.getWidth() / 2) {
                        // carve out vertical corridor from start to end.
                        // Just for debugging
                        // System.out.println("Carving vertical corridor from "+i+" to "+ j);
                        worldRep = emptyOutCellsVertical(
                                worldRep, startCoords[0], startCoords[1], endCoords[1]
                        );
                    } else if ((Math.abs(startCoords[1] - endCoords[1])
                            <= endRoom.getHeight() / 2)) {
                        // Carve out horizontal corridor from start to end.
                        // System.out.println("Carving horizontal corridor from "+i+" to "+ j);
                        worldRep = emptyOutCellsHorizontal(
                                worldRep, startCoords[1], startCoords[0], endCoords[0]
                        );
                    } else {
                        // carve out L-shaped corridor.
                        // System.out.println("Carving L-shaped corridor from "+i+" to "+ j);
                        // carve out row horizontally from start[0] to end[0] on the start row
                        worldRep = emptyOutCellsHorizontal(
                                worldRep, startCoords[1], startCoords[0], endCoords[0]
                        );
                        // carve out column vertically from start[1] to end[1] on the end column
                        worldRep = emptyOutCellsVertical(
                                worldRep, endCoords[0], startCoords[1], endCoords[1]
                        );
                    }
                }
            }
        }
        return worldRep;
    }

    /**
     * Helper method for generateHallways.
     * Given an int matrix and X coordinate, and start and end coordinates for Y, sets
     * a vertical line in the matrix to 1s.
     * Note that the input is in cartesian coordinates.
     *
     * @param matrix
     * @param X
     * @param startY
     * @param endY
     */
    private int[][] emptyOutCellsVertical(int[][] matrix, int X, int startY, int endY) {
        int start;
        int end;
        if (startY < endY) {
            start = startY;
            end = endY;
        } else {
            start = endY;
            end = startY;
        }
        int height = matrix.length;
        for (int i = start; i <= end; i++) {
            // matrix[height-1-i][X] = 1;
            matrix[i][X] = 1;
        }
        return matrix;
    }

    /**
     * Helper method for generateHallways.
     * Given an int matrix and Y coordinate, and start and end coordinates for X, sets
     * a horizontal line in the matrix to 1s.
     * Note that the input is in cartesian coordinates.
     *
     * @param matrix
     * @param Y
     * @param startX
     * @param endX
     */
    private int[][] emptyOutCellsHorizontal(int[][] matrix, int Y, int startX, int endX) {
        int start;
        int end;
        if (startX < endX) {
            start = startX;
            end = endX;
        } else {
            start = endX;
            end = startX;
        }

        int height = matrix.length;

        for (int i = start; i <= end; i++) {
            // matrix[height-1-Y][i] = 1;
            matrix[Y][i] = 1;
        }
        return matrix;
    }

    /**
     * Helper method. Given a roomGraph, return an int array representation of just the rooms.
     * 0 represents a "wall" while 1 represents walkable space.
     *
     * @param roomGraph
     * @return
     */
    private int[][] emptyOutRooms(RoomGraph roomGraph) {
        // We represent the TETile matrix with an int matrix first.
        // 0 represents a "wall" while 1 represents a walkable space.

        int[][] worldRep = new int[_height][_width];

        // Carve out space for each room.
        List<Room> roomList = roomGraph.getRoomList();
        for (Room r : roomList) {
            int roomHeight = r.getHeight();
            int roomWidth = r.getWidth();
            int roomX = r.getX();
            int roomY = r.getY();

            for (int i = 0; i < roomHeight; i++) {
                for (int j = 0; j < roomWidth; j++) {
                    // empty out space, converting from cartesian to matrix coords
                    // note that we only have to flip y coords, not x
                    worldRep[roomY + 1 + i][roomX + 1 + j] = 1;
                }
            }
        }
        return worldRep;
    }

    //TODO: change below method back to private
    /**
     * Converts an int[][] representing a world into a TETile[][] to prepare for rendering.
     *
     * @param worldRep
     * @return converted TETile array
     */
    public TETile[][] convertToTiles(int[][] worldRep) {
        TETile[][] toReturn = new TETile[worldRep[0].length][worldRep.length];
        for (int i = 0; i < worldRep.length; i++) {
            for (int j = 0; j < worldRep[i].length; j++) {
                if (worldRep[i][j] == 0) { // non-walkable
                    toReturn[j][i] = WALL;
                } else if (worldRep[i][j] == 1) {
                    toReturn[j][i] = FLOOR;
                }
            }
        }
        return toReturn;
    }
}
