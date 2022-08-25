package byow;

import byow.TileEngine.TETile;

import static byow.TileEngine.Tileset.FLOOR;
import static byow.TileEngine.Tileset.WALL;

public class CrawlerTests {

    public TETile[][] makeMaze() {
        TETile[][] world = new TETile[10][12];
        for (int i=0; i<10; i++) {
            for (int j=0; j<12; j++) {
                world[i][j] = WALL;
            }
        }

        for (int i=1; i<9; i++) {
            world[i][1] = FLOOR;
            world[i][8] = FLOOR;
            world[i][4] = FLOOR;
            world[4][i] = FLOOR;
        }

        for (int i=0; i<3; i++) {
            world[1][i+2] = FLOOR;
            world[7][i+5] = FLOOR;
        }
        world[5][2] = FLOOR;

        return world;
    }
}
