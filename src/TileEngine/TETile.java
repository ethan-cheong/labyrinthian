package byow.TileEngine;

import byow.Core.RandomUtils;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * The TETile object is used to represent a single tile in your world. A 2D array of tiles make up a
 * board, and can be drawn to the screen using the TERenderer class.
 *
 * All TETile objects must have a character, textcolor, and background color to be used to represent
 * the tile when drawn to the screen. You can also optionally provide a path to an image file of an
 * appropriate size (16x16) to be drawn in place of the unicode representation. If the image path
 * provided cannot be found, draw will fallback to using the provided character and color
 * representation, so you are free to use image tiles on your own computer.
 *
 * The provided TETile is immutable, i.e. none of its instance variables can change. You are welcome
 * to make your TETile class mutable, if you prefer.
 */

public class TETile implements Serializable {
    private final char character; // Do not rename character or the autograder will break.
    private final Color textColor;
    private final Color backgroundColor;
    private final String description;
    private final String filepath;
    private final boolean isDark; // indicates if the tile is lit up or not
    private final boolean isPath;
    /**
     * Full constructor for TETile objects.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     * @param filepath Full path to image to be used for this tile. Must be correct size (16x16)
     */
    public TETile(char character, Color textColor, Color backgroundColor, String description,
                  String filepath, boolean isDark, boolean isPath) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = filepath;
        this.isDark = isDark;
        this.isPath = isPath;
    }

    /**
     * Constructor without filepath. In this case, filepath will be null, so when drawing, we
     * will not even try to draw an image, and will instead use the provided character and colors.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     */
    public TETile(char character, Color textColor, Color backgroundColor,
                  String description, boolean isDark, boolean isPath) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = null;
        this.isDark = isDark;
        this.isPath = isPath;
    }

    /**
     * Constructor without filepath or isDark or isPath.
     * In this case, filepath will be null, so when drawing, we will not even try to draw an
     * image, and will instead use the provided character and colors.
     * Tiles will be dark by default.
     * Tiles will not be paths by default.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     */
    public TETile(char character, Color textColor, Color backgroundColor,
                  String description) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = null;
        this.isDark = true;
        this.isPath = false;
    }

    /**
     * Creates a copy of TETile t, except with given textColor.
     * @param t tile to copy
     * @param textColor foreground color for tile copy
     */
    public TETile(TETile t, Color textColor) {
        this(t.character, textColor, t.backgroundColor, t.description, t.filepath, t.isDark,
                t.isPath);
    }

    /**
     * Creates a copy of this TETile.
     */
    public TETile copyOf(){
        return new TETile(character, textColor, backgroundColor, description, filepath, isDark,
                isPath);
    }


    /**
     * Draws the tile to the screen at location x, y. If a valid filepath is provided,
     * we draw the image located at that filepath to the screen. Otherwise, we fall
     * back to the character and color representation for the tile.
     *
     * Note that the image provided must be of the right size (16x16). It will not be
     * automatically resized or truncated.
     * @param x x coordinate
     * @param y y coordinate
     * @param renderPaths if true, paths are drawn in red, even when dark.
     */
    public void draw(double x, double y, boolean renderPaths) {
        if (filepath != null) {
            try {
                StdDraw.picture(x + 0.5, y + 0.5, filepath);
                return;
            } catch (IllegalArgumentException e) {
                // Exception happens because the file can't be found. In this case, fail silently
                // and just use the character and background color for the tile.
            }
        }

        /* render paths, then dark tiles, then normal tiles */
        if (renderPaths && this.isPath && character != '@' && character != '░') {
            StdDraw.setPenColor(Color.red);
        } else if (this.isDark) {
            StdDraw.setPenColor(Color.black);
        } else  {
            StdDraw.setPenColor(backgroundColor);
        }

        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
        if (renderPaths && this.isPath) {
            StdDraw.setPenColor(Color.red);
        } else if (this.isDark) {
            StdDraw.setPenColor(Color.black);
        } else {
            StdDraw.setPenColor(textColor);
        }
        StdDraw.text(x + 0.5, y + 0.5, Character.toString(character()));
    }

    /** Character representation of the tile. Used for drawing in text mode.
     * @return character representation
     */
    public char character() {
        return character;
    }

    /**
     * Description of the tile. Useful for displaying mouseover text or
     * testing that two tiles represent the same type of thing.
     * @return description of the tile
     */
    public String description() {
        return description;
    }

    /**
     * Creates a copy of the given tile with a slightly different text color. The new
     * color will have a red value that is within dr of the current red value,
     * and likewise with dg and db.
     * @param t the tile to copy
     * @param dr the maximum difference in red value
     * @param dg the maximum difference in green value
     * @param db the maximum difference in blue value
     * @param r the random number generator to use
     */
    public static TETile colorVariant(TETile t, int dr, int dg, int db, Random r) {
        Color oldColor = t.textColor;
        int newRed = newColorValue(oldColor.getRed(), dr, r);
        int newGreen = newColorValue(oldColor.getGreen(), dg, r);
        int newBlue = newColorValue(oldColor.getBlue(), db, r);

        Color c = new Color(newRed, newGreen, newBlue);

        return new TETile(t, c);
    }

    private static int newColorValue(int v, int dv, Random r) {
        int rawNewValue = v + RandomUtils.uniform(r, -dv, dv + 1);

        // make sure value doesn't fall outside of the range 0 to 255.
        int newValue = Math.min(255, Math.max(0, rawNewValue));
        return newValue;
    }

    /**
     * Converts the given 2D array to a String. Handy for debugging.
     * Note that since y = 0 is actually the bottom of your world when
     * drawn using the tile rendering engine, this print method has to
     * print in what might seem like backwards order (so that the 0th
     * row gets printed last).
     * @param world the 2D world to print
     * @return string representation of the world
     */
    public static String toString(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        StringBuilder sb = new StringBuilder();

        for (int y = height - 1; y >= 0; y -= 1) {
            for (int x = 0; x < width; x += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                if (world[x][y].isPath()) {
                    sb.append("p"); // show paths specially
                } else {
                    sb.append(world[x][y].character());
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Makes a copy of the given 2D tile array.
     * @param tiles the 2D array to copy
     **/
    public static TETile[][] copyOf(TETile[][] tiles) {
        if (tiles == null) {
            return null;
        }

        TETile[][] copy = new TETile[tiles.length][];

        int i = 0;
        for (TETile[] column : tiles) {
            copy[i] = Arrays.copyOf(column, column.length);
            i += 1;
        }
        return copy;
    }


    /**
     * Takes the TETile and returns a copy of it with isDark flipped.
     */
    public TETile copyOfDarkFlipped() {
        return new TETile(character, textColor,
                backgroundColor, description, filepath, !isDark, isPath);
    }

    /**
     * Takes the TETile and returns a copy of it with isPath flipped.
     * @return
     */
    public TETile copyOfPath() {
        return new TETile(character, textColor,
                backgroundColor, description, filepath, isDark, true);
    }

    /**
     * Takes the TETile and returns a copy of it which is not a path
     * @return
     */
    public TETile copyOfNotPath() {
        return new TETile(character, textColor,
                backgroundColor, description, filepath, isDark, false);
    }

    /**
     * Returns true if the current tile is dark.
     */
    public boolean isDark() {
        return isDark;
    }

    /**
     * Returns true if the current tile is a path.
     */
    public boolean isPath() {
        return isPath;
    }

    /**
     * Equals for TETiles should check the CHARACTER.
     */
    @Override
    public boolean equals(Object o) {
        if (o==null) {
            return false;
        }
        if (o==this) {
            return true;
        }
        if (o instanceof TETile) {
            TETile otherTile = (TETile) o;
            return otherTile.character() == this.character;
        }
        return false;
    }
}
