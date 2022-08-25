package byow.Core;

/**
 * Class that represents a room in the world.
 * This room has height 1 and width 2:
 * xxxx
 * x--x
 * xxxx
 */
public class Room {
    private int _height;
    private int _width;

    /**
     * Coordinates below refer to the BOTTOM LEFT square of the room.
     **/
    private int _x;
    private int _y;

    public Room(int height, int width, int x, int y) {
        if (height <= 0 || width <= 0 || x < 0 || y < 0) {
            throw new IllegalArgumentException("Room initialized with inappropriate arguments");
        }
        _height = height;
        _width = width;
        _x = x;
        _y = y;
    }

    /**
     * Returns the height of the room.
     *
     * @return height
     */
    public int getHeight() {
        return _height;
    }

    /**
     * Returns the width of the room.
     *
     * @return width
     */
    public int getWidth() {
        return _width;
    }

    /**
     * Returns the x-coordinate of the lower left wall of the room.
     *
     * @return x
     */
    public int getX() {
        return _x;
    }

    /**
     * Returns the y-coordinate of the lower left wall of the room.
     *
     * @return y
     */
    public int getY() {
        return _y;
    }

    /**
     * Returns the cartesian coordinates of the center of the room, in the order [x, y].
     * Snaps to the top right if we have even width/height.
     * So the room below has Center [1,1]
     * xxxx
     * x--x
     * x--x
     * xxxx
     *
     * @return [x, y]
     */
    public int[] getCenter() {
        int[] toReturn = new int[2];
        toReturn[0] = _x + _width / 2 + 1;
        toReturn[1] = _y + _height / 2 + 1;
        return toReturn;
    }

    /**
     * Returns a string representation of the room.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        int printHorizontal = _width + 2;
        for (int i = 0; i < printHorizontal; i++) {
            output.append('x');
        }
        output.append('\n');

        for (int i = 0; i < _height; i++) {
            output.append('x');
            for (int j = 0; j < _width; j++) {
                output.append('-');
            }
            output.append("x\n");
        }

        for (int i = 0; i < printHorizontal; i++) {
            output.append('x');
        }
        output.append('\n');

        return output.toString();
    }
}
