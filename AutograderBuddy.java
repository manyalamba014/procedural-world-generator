package core;
import tileengine.TETile;
import tileengine.Tileset;

import java.io.*;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param str the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String str) {
        //standardize the sstring
        str = str.toUpperCase();
        String seed = "";
        World world = new World(0);
        int inc;
        //loop thru the command string
        for (int i = 0; i < str.length(); i += inc) {
            inc = 1;
            char c = str.charAt(i);
            //if N, get a new seed
            if (c == 'N') {
                seed = "";
            } else if (Character.isDigit(c)) {
                //if you have a number, it should be added to the seed
                seed += c;
                //that is,  until it hits an S.
                if (i + 1 < str.length() && str.charAt(i + 1) == 'S') {
                    world = new World(Long.parseLong(seed));
                    System.out.println("New world made w seed:" + seed);
                    inc = 2;
                }
            } else {
                //moves the avatar or  loads the game
                if (c == 'W') {
                    world.updateAvatar(0, 1);
                } else if (c == 'S') {
                    world.updateAvatar(0, -1);
                } else if (c == 'A') {
                    world.updateAvatar(-1, 0);
                } else if (c == 'D') {
                    world.updateAvatar(1, 0);
                } else if (c == 'L') {
                    world = loadWorld();
                }
                //saves
                if (c == ':' && i + 1 < str.length() && str.charAt(i + 1) == 'Q') {
                    saveWorld(world);
                    inc = 2;
                }
            }
        }
        return world.getWorld();
    }


    //saves world
    public static void saveWorld(World world) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./savedWorld.txt"))) {
            out.writeObject(world);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static World loadWorld() {
        File file = new File("./savedWorld.txt");
        World world = new World(0);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                world = (World) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(2);
            }
        }
        return world;
    }

    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile tile) {
        return tile.character() == Tileset.FLOOR.character()
                || tile.character() == Tileset.AVATAR.character()
                || tile.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile tile) {
        return tile.character() == Tileset.WALL.character()
                || tile.character() == Tileset.LOCKED_DOOR.character()
                || tile.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
