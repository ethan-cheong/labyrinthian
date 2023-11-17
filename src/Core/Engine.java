package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.Random;

/**
 * The engine handles key inputs and visualizing the world.
 */
public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 48;

    public static final File SAVE_FILE = new File(System.getProperty("user.dir"), "save.txt");

    private static final String[] randomDialogue = {"It's so dark!", "What's that noise?",
            "I'm tired.", "Wasn't I just here?", "I need a lamp...", "hm", "..."};

    /* Indicates whether the engine will render paths taken by enemies. */
    private boolean renderPaths = false;


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        drawMenu();

        //call gatherUserInput to get what user typed, input should be a single character right now
        char input = gatherUserInput(new char[] {'N', 'Q', 'L', 'S', 'n', 'q', 'l' ,'s'});
        switch (input) {
            case 'N','n':
                runGame(false);
                return;

            case 'L','l':
                runGame(true);
                return;
            //add case where there's no previous world, throw error
            case 'Q','q':
                return;
        }
    }

    /**
     * Draw the menu screen.
     */
    public void drawMenu() {
        //draw the startup screen: should be black background with white font, CS61B: THE GAME, New Game (N), Load Game (L), Quit (Q)
        //make it black background
        StdDraw.clear(Color.BLACK);
        //set canvas size
        StdDraw.setCanvasSize(WIDTH*16, HEIGHT*16);
        //set pen color to white
        StdDraw.setPenColor(Color.WHITE);
        //set to cool font
        Font f = new Font("Narnia", Font.BOLD, 50);
        StdDraw.setFont(f);
        //set scales
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        //width should always be width/2 to be centered, height might need to be adjusted
        StdDraw.text(WIDTH/2, HEIGHT/1.2, "Labyrinthian");
        Font f2 = new Font("Narnia", Font.BOLD, 30);
        StdDraw.setFont(f2);
        StdDraw.text(WIDTH/2, HEIGHT/1.5, "New Game (N)"); //1.43
        StdDraw.text(WIDTH/2, HEIGHT/2, "Load Game (L)"); //2
        StdDraw.text(WIDTH/2, HEIGHT/3, "Quit (Q)");  //3.33
        //calling show allows everything to appear simultaneously instead of one-by-one
        StdDraw.show();
    }

    /**
     * Loads a new game after prompting when "N" is pressed in the menu.
     */
    public void runGame(boolean load) {
        Game game;
        if (!load) {
            // print out prompt screen for seed
            long seed = inputSeedScreen();
            Difficulty difficulty = difficultySelectScreen();
            WorldGenerator w = new WorldGenerator(WIDTH, HEIGHT, seed, difficulty);
            TETile[][] world = w.generateWorld();
            Random gameRandom = new Random(seed);
            game = new Game(world, gameRandom, difficulty);
            world = game.getWorld();
            ter.setDefaultParams();
            ter.initialize(WIDTH, HEIGHT+2, 0, 2);
            ter.renderFrame(world, renderPaths);
            // make a new Game object using the TETile array
        } else {
            game = loadGame();
            if (game==null) { // if loading the game fails
                return;
            }
            ter.setDefaultParams();
            ter.initialize(WIDTH, HEIGHT+2, 0, 2);
            TETile[][] world = game.getWorld();
            ter.renderFrame(world, renderPaths);
        }
        Outcome outcome = null;
        while (game.isRunning()) {
             int mouseX = (int) StdDraw.mouseX();
             int mouseY = (int) StdDraw.mouseY();
             if ((mouseX <= WIDTH && mouseY <= HEIGHT) && mouseY >= 2) {
                 TETile[][] world = game.getWorld();
                 TETile currentTile = world[mouseX][mouseY-2]; // add in mouseY-2 if necessary
                 String tileDescription;
                 if (currentTile.isDark()) {
                     tileDescription = "It's so dark...";
                 } else {
                     tileDescription = currentTile.description();
                 }
                 //render HUD
                 Font f = new Font("Narnia", Font.BOLD, 12);
                 StdDraw.setFont(f);
                 StdDraw.setPenColor(Color.WHITE);
                 StdDraw.text(5, 1, tileDescription);
                 StdDraw.show();
             }

            StdDraw.setPenColor(Color.WHITE);
            Font f = new Font("Narnia", Font.BOLD, 12);
            StdDraw.setFont(f);
            int randomSentences = RandomUtils.uniform(game._random, randomDialogue.length);
            StdDraw.text(WIDTH/2, 1, randomDialogue[randomSentences]);
            String save = "CURRENT DREAD: " + game._player.getDread();
            StdDraw.text(WIDTH/1.15, 1, save);
            StdDraw.show();

            char input = gatherUserInput(new char[] {':', 'w', 'a', 's', 'd'});
            if (input==':') {
                // "preparing for special input"
                input = gatherUserInput();
                if (input == 'q' || input == 'Q') {
                    saveGame(game);
                    return;
                }
                if (input == 'l' || input == 'L') {
                    game.toggleDark();
                    TETile[][] world = game.getWorld();
                    ter.setDefaultParams();
                    ter.renderFrame(world, renderPaths);
                    continue;
                }
                if (input == 'p' || input == 'P') {
                    /* Toggle path rendering */
                    renderPaths = !renderPaths;
                    TETile[][] world = game.getWorld();
                    ter.setDefaultParams();
                    ter.renderFrame(world, renderPaths);
                    continue;
                }
                if (input == 'h' || input == 'H') {
                    /* show the help screen */
                    helpScreen();
                    TETile[][] world = game.getWorld();
                    ter.initialize(WIDTH, HEIGHT+2, 0, 2);
                    ter.setDefaultParams();
                    ter.renderFrame(world, renderPaths);
                    continue;
                }
                continue; // restart animation loop
            }
            // we only reach here if we inputted wasd
            outcome = game.tickTime(input);
            TETile[][] world = game.getWorld();
            ter.setDefaultParams();
            ter.renderFrame(world, renderPaths);

        }
        // when game no longer runs (reach this point)
        assert outcome != null;
        gameOverScreen(outcome);
        // print game over screen.
    }

    /**
     * Loads the screen and prompts user for an input seed. If no input is given, returns the
     * default seed (0L).
     * @return
     */
    private long inputSeedScreen(){
        // query user for input one at a time, build up, print on screen
        StringBuilder seedInput = new StringBuilder();
        char currentInput='\u0000'; // this is just a placeholder
        while (currentInput != 's' && currentInput != 'S') {
            if (currentInput != '\u0000') {
                seedInput.append(currentInput);
            }
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            Font fontBig = new Font("Narnia", Font.BOLD, 30);
            StdDraw.setFont(fontBig);
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 3 * 2, "Give a seed number:");
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, seedInput.toString());
            StdDraw.show();
            currentInput = gatherUserInput(new char[] {'0','1','2','3','4','5','6','7','8','9','S','s'});
        }

        if (seedInput.length()!=0) {
            return Long.parseLong(seedInput.toString());
        }

        return 0L;

    }

    /**
     * Loads the difficulty screen and prompts user to select difficulty.
     * @return
     */
    private Difficulty difficultySelectScreen(){
        // query user for input one at a time, build up, print on screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Narnia", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 3 * 2, "Select the difficulty.");
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 3, "e: Easy, m: Medium, h: Hard");
        StdDraw.show();
        char input = gatherUserInput(new char[] {'e', 'm', 'h', 'E','M','H'});

        switch (input) {
            case 'e','E':
                return Difficulty.EASY;
            case 'm', 'M':
                return Difficulty.MEDIUM;
            case 'h','H':
                return Difficulty.HARD;
            default:
                return Difficulty.EASY;
        }
    }

    /**
     * Displays controls and instructions about the game.
     */
    private void helpScreen() {
        ter.initialize(WIDTH, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Narnia", Font.BOLD, 30);
        Font fontSmall = new Font("Narnia", Font.BOLD, 15);
        StdDraw.setFont(fontBig);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 8 * 7, "HELP");
        StdDraw.setFont(fontSmall);
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3, "Controls");
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3 - 4, "w - Move up");
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3 - 6, "s - Move down");
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3 - 8, "a - Move left");
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3 - 10, "d - Move right");
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3 - 12, ":q / :Q - Save and quit");
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3 - 14, ":h / :H - Help menu");
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3 - 16, ":l / :L - Toggle lighting");
        StdDraw.text((double) WIDTH / 4, (double) HEIGHT / 4 * 3 - 18, ":p / :P - Show enemy " +
                "paths");
        StdDraw.text((double) WIDTH / 4 * 3, (double) HEIGHT / 4 * 3, "This is you: @");
        StdDraw.text((double) WIDTH / 4 * 3, (double) HEIGHT / 4 * 3 - 2, "Avoid enemies if you " +
                "want to " + "survive.");
        StdDraw.text((double) WIDTH / 4 * 3, (double) HEIGHT / 4 * 3 - 4, "Your dread grows " +
                "the longer you stay in the dark.");
        StdDraw.setPenColor(Color.yellow);
        StdDraw.text((double) WIDTH / 4 * 3, (double) HEIGHT / 4 * 3 - 18, "I need to get out of " +
                "this place...");

        StdDraw.show();
        char input = gatherUserInput(new char[] {'q', 'Q'});
    }

    /**
     * Displays controls and instructions about the game.
     */
    public void gameOverScreen(Outcome outcome) {
        ter.initialize(WIDTH, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(outcome.MESSAGE_COLOR);
        Font fontBig = new Font("Narnia", Font.BOLD, 30);
        Font fontSmall = new Font("Narnia", Font.BOLD, 15);
        StdDraw.setFont(fontBig);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 3 * 2 , outcome.MESSAGE);
        StdDraw.setFont(fontSmall);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 3, "Press q to quit.");
        StdDraw.show();
    }

    /**
     * Displays controls and instructions about the game, but doesn't prompt user for input.
     */
    private void printHelpScreen(){
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Narnia", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 3 * 2, "This is the instruction screen");
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 3, "e: Easy, m: Medium, h: Hard");
        StdDraw.show();
    }

    /**
     *  Saves the game to the ./saves directory, making it if it doesn't exist.
     *
     */
    private void saveGame(Game game) {
        Utils.writeObject(SAVE_FILE, game);
    }

    /**
     * Loads the game from the ./saves directory.
     * @return null if no save files exist; otherwise, returns the current save file.
     */
    private Game loadGame() {
        if (!SAVE_FILE.exists()) {
            return null;
        }
        return Utils.readObject(SAVE_FILE, Game.class);

    }

    /**
     * Returns a single char consisting of the user's next input, provided it's in validChars.
     * Wait until a character in validChars is entered.
     */
    private char gatherUserInput(char[] validChars) {
        HashSet<Character> valid = new HashSet<>();
        for (char c : validChars) {
            valid.add(c);
        }
        // waiting for any keypress...
        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.pause(1); // keep waiting until any key is pressed
        }

        // only one keypress for this method
        while (true) {
            char typedCharacter = StdDraw.nextKeyTyped();
            if (valid.contains(typedCharacter)) {
                return typedCharacter;
            } else {
                while(!StdDraw.hasNextKeyTyped()) {
                    StdDraw.pause(1);
                }
            }
        }
    }

    /**
     * Returns a char consisting of a single user input.
     */
    private char gatherUserInput() {

        //waiting for any keypress...
        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.pause(1); //we'll keep waiting until any key is pressed
        }

        return StdDraw.nextKeyTyped();
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {

        InputSource inputSource = new StringInputDevice(input);

        int totalCharacters = 0;

        String seed = "";
        boolean mainMenu = true;
        boolean readSeed = false;
        boolean selectDifficulty = false;
        boolean inGame = false;
        String commands = "";
        boolean prepSpecial = false;
        boolean helpScreen = false;

        Game game = loadGame(); // trick to get compiler to work

        while (inputSource.possibleNextInput()) {
            totalCharacters += 1;
            char c = inputSource.getNextKey();
            if ((c == 'l' || c == 'L') && mainMenu) {
                // load the game
                game = loadGame();
                if (game==null) {
                    return null;
                }
                mainMenu = false;
                readSeed = false;
                inGame = true;
                continue;
            }
            if ((c == 'n' || c == 'N') && mainMenu) {
                mainMenu = false;
                readSeed = true;
                continue;
            }
            if (Character.isDigit(c) && readSeed) {
                seed += c;
                continue;
            }
            if ((c == 's' || c == 'S') && readSeed) {
                readSeed = false;
                selectDifficulty = true;
            }
            if ((c == 'e' || c == 'E' || c == 'm' || c == 'M' || c == 'h' || c == 'H') &&
                    selectDifficulty) {
                Difficulty difficulty;
                switch (c) {
                    case 'e','E':
                         difficulty = Difficulty.EASY;
                         break;
                    case 'm', 'M':
                        difficulty = Difficulty.MEDIUM;
                        break;
                    case 'h','H':
                        difficulty = Difficulty.HARD;
                        break;
                    default:
                        difficulty = Difficulty.EASY;
                }
                selectDifficulty = false;
                inGame = true;
                WorldGenerator w = new WorldGenerator(WIDTH, HEIGHT, Long.parseLong(seed), difficulty);
                TETile[][] world = w.generateWorld();
                Random gameRandom = new Random(Long.parseLong(seed));
                game = new Game(world, gameRandom, difficulty);
                continue;
            }
            if ((c == 's' || c == 'S' || c == 'w' || c == 'W'
                    || c == 'a' || c == 'A' || c == 'd' || c == 'D') && inGame) {
                commands += c;
                game.tickTime(c);
                continue;
            }
            if (c == ':' && inGame) {
                prepSpecial= true;
                continue;
            }
            if ((c == 'q' || c == 'Q') && prepSpecial) {
                saveGame(game);
                break;
            }
            if ((c == 'l' || c == 'L') && prepSpecial) {
                game.toggleDark();
                prepSpecial = false;
                continue;
            }
            if ((c == 'p' || c == 'P') && prepSpecial) {
                renderPaths = !renderPaths;
                prepSpecial = false;
                continue;
            }
            if ((c=='h' || c == 'H') && prepSpecial) {
                inGame = false;
                helpScreen = true;
                printHelpScreen();
            }
            if (helpScreen && (c == 'q' || c == 'Q')) {
                inGame = true;
                helpScreen = false;
            }
            if (prepSpecial) {
                // If we reach here it means we input an invalid special command
                prepSpecial = false;
                continue;
            }
        }
        TETile[][] world = game.getWorld();
        //System.out.println(TETile.toString(world));
        return game.getWorld();
    }
}
