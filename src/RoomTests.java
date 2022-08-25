package byow;

import byow.Core.Room;
import org.junit.Test;

import java.util.ArrayList;

public class RoomTests {
    @Test
    public void roomTest1() {
        Room r1 = new Room(2, 10, 0, 0);
        System.out.println(r1);
        System.out.println("x: " + r1.getCenter()[0] + ", y: " + r1.getCenter()[1]);
    }

    @Test
    public void roomGraphTest1() {
        Room r0 = new Room(1, 1, 2, 1);
        Room r1 = new Room(2, 4, 7, 2);
        Room r2 = new Room(3, 2, 6, 9);
        Room r3 = new Room(1, 1, 0, 9);
        ArrayList<Room> roomList = new ArrayList<>();
        roomList.add(r0);
        roomList.add(r1);
        roomList.add(r2);
        roomList.add(r3);
    }
}


