package byow.TileEngine;

import java.awt.*;
import java.io.Serializable;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset implements Serializable {
    public static final TETile AVATAR = new TETile('@', Color.black, Color.gray, "you", false,
            false);
    public static final TETile WALL = new TETile('#', new Color(135,206,250), new Color(0, 0, 139),
            "wall");
    public static final TETile FLOOR = new TETile(' ', new Color(128, 192, 128), Color.gray,
            "floor");
    public static final TETile EXIT = new TETile('*', Color.yellow, Color.black, "I want to go " +
            "home...");
    /* PATH tile used by enemies */
    public static final TETile CRAWLER = new TETile('░', Color.RED, Color.gray, "crawler");
    public static final TETile CRAWLER_AGGRO = new TETile('▓', Color.red, Color.gray, "AHHHHHHH");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
}


