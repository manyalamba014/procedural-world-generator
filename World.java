package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.io.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class World implements Serializable {
    //basic widths and heights.
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private TETile[][] world;
    private List<Room> rooms;
    private Random random;

    private Avatar avatar;


    //constructor
    public World(long seed) {
        this.world = new TETile[WIDTH][HEIGHT];
        this.rooms = new ArrayList<>();
        this.random = new Random(seed);
        fillWithNothingTiles();
        createRoomsAndHallways();
        addWalls();

        int startingx = 0;  // Default starting x-coordinate
        int startingy = 0;  // Default starting y-coordinate
        // Start from the bottom-right corner of the world grid
        full:
        for (int i = 0; i < WIDTH; i++) {
            for (int j = WIDTH - 1; j >= 0; j--) {
                if (isWalkable(i, j)) {
                    startingx = i;
                    startingy = j;
                    world[startingx][startingy] = Tileset.AVATAR;  // Place the avatar at the first walkable spot found
                    this.avatar = new Avatar(startingx, startingy, this); // Return the new avatar
                    break full;
                }
            }
        }
    }


    //updates the avatar's positioning
    public Avatar updateAvatar(int dx, int dy) {

        //makes the tile that the avatar just left a floor tile
        world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
        //moves the avatar by specified amount
        avatar.move(dx, dy);


        //makes the avatar's new possitioning an avatar tileset
        world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
        return avatar;
    }

    //fill with eempty tiles to default
    private void fillWithNothingTiles() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }


    //create  rooms and hallways
    private void createRoomsAndHallways() {
        //rooms should be between 10 and 19. this should basically guarantee ovr 50% fill rate b/c of
        //the next few lines wher we predtermine width and height.
        final int base = 10;
        final int upscale = 10;
        int n = base + random.nextInt(upscale); // Ensure more than 50% of the world is filled
        for (int i = 0; i < n; i++) {
            //see above comments. also, this ensures a rectangle
            int w = RandomUtils.uniform(random, 3, 8);
            int h = RandomUtils.uniform(random, 3, 8);
            //decides room coordinates. bounds are to ensure no edge clipping
            int x = RandomUtils.uniform(random, 1, WIDTH - w - 1);
            int y = RandomUtils.uniform(random, 1, HEIGHT - h - 1);
            Room r = new Room(x, y, w, h);
            //if the rooms dont overlap, add it to th list
            if (noOverlap(r)) {
                rooms.add(r);
                //populate the room withfloor tiles
                for (int dx = 0; dx < w; dx++) {
                    for (int dy = 0; dy < h; dy++) {
                        world[x + dx][y + dy] = Tileset.FLOOR;
                    }
                }
            }
        }

        // connect rooms with hallways
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room r1 = rooms.get(i);
            Room r2 = rooms.get(i + 1);
            //hallways connect to room centeers
            int startX = r1.centerX();
            int startY = r1.centerY();
            int endX = r2.centerX();
            int endY = r2.centerY();

            // for horizontal hallways
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
                world[x][startY] = Tileset.FLOOR;
            }
            // for ertical hallways from the end of the horizontal hallway to the second room
            for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
                world[endX][y] = Tileset.FLOOR;
            }
        }
    }

    //checks to make sure rooms dont overlap. see room class below for more details
    private boolean noOverlap(Room newRoom) {
        for (Room r : rooms) {
            if (newRoom.overlaps(r)) {
                return false;
            }
        }
        return true;
    }


    //ads walls.
    private void addWalls() {
        //for every tile, we basically check that for any floor tile, you make a wall layeer.
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (world[x + dx][y + dy] == Tileset.NOTHING) {
                                world[x + dx][y + dy] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
        }
    }


    //gets a tiile at the specificed coordinates
    public TETile getTile(int x, int y) {
        return world[x][y];
    }



    // room private helper class to manage rooms
    private class Room implements Serializable {
        int x, y, width, height;
        public Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        //checks if the horizotnaland vertical ranges overlap, bc that implies they do.
        boolean overlaps(Room other) {
            return x < other.x + other.width && x + width > other.x
                    && y < other.y + other.height && y + height > other.y;
        }

        int centerX() {
            return x + width / 2;
        }

        int centerY() {
            return y + height / 2;
        }
    }

    public class Avatar implements Serializable {

        //helper class forr the avatar
        private int x, y;
        private World world;

        public Avatar(int startX, int startY, World world) {
            this.x = startX;
            this.y = startY;
            this.world = world;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }


        public void move(int dx, int dy) {
            int newX = x + dx;
            int newY = y + dy;
            //uses iswalkable method to help decide if a move is even valid or not

            boolean a = isWithinBounds(newX, newY);
            boolean b = world.isWalkable(newX, newY);
            if (a && b) {
                x = newX;
                y = newY;
            }
        }
    }

    public boolean isWithinBounds(int x, int y) {
        final int zero = 0;
        final int eighty = 80;
        final int thirty = 30;
        return x >= zero && x < eighty && y >= zero && y < thirty;
    }

    // Checks if the tile at the given position is walkable
    public boolean isWalkable(int x, int y) {
        if (!isWithinBounds(x, y)) {
            return false;
        }
        TETile t = world[x][y];
        return t.equals(Tileset.FLOOR) || t.equals(Tileset.GRASS)
                || t.equals(Tileset.UNLOCKED_DOOR) || t.equals(Tileset.AVATAR);
    }




    public TETile[][] getWorld() {
        return world;
    }
}
