package byow.Core;

/**
 * Used by the player and NPCs to pass information about their next movement to the Game.
 * NORTH, SOUTH, EAST and WEST, with NORTH pointing to the top of the screen.
 * Entities return NOPE when they are not moving.
 */
public enum Direction {
    NORTH {
        @Override
        public String toString() {
            return "N";
        }
    },
    SOUTH {
        @Override
        public String toString() {
            return "S";
        }
    },
    EAST {
        @Override
        public String toString() {
            return "E";
        }
    },
    WEST {
        @Override
        public String toString() {
            return "W";
        }
    },
    NOPE {
        @Override
        public String toString() {
            return "x";
        }
    }

}
