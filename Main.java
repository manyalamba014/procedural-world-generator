package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class Main {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private TERenderer ter = new TERenderer();
    private World world;

    public static void main(String[] args) {
        Main game = new Main();
        game.menu();
        game.initialize();
        game.run();
    }

    private void menu() {
        // Constants for screen dimensions and text positions
        final int tileSize = 16;  // Assuming each tile is 16x16 pixels

        final double textX = WIDTH / 2.0; // Central x position for text
        final double textLoadY = HEIGHT * 0.5; // y position for "Load Previous Save"
        final double textStartY = HEIGHT * 0.4; // y position for "Start Game"
        final double textExitY = HEIGHT * 0.3;  // y position for "Exit"

        StdDraw.setCanvasSize(WIDTH * tileSize, HEIGHT * tileSize);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);

        StdDraw.clear(StdDraw.BLACK);

        StdDraw.setPenColor(StdDraw.WHITE);

        StdDraw.text(textX, textLoadY, "Load Previous Save (L)");
        StdDraw.text(textX, textExitY, "Exit (Q)");
        StdDraw.text(textX, textStartY, "Start Game (N)");
        boolean running = true;
        while (running) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == 'Q') {
                    final int code = 10;
                    System.exit(code);
                } else if (key == 'L') {
                    loadWorld();
                    running = false;
                } else if (key == 'N') {
                    running = false;
                    StdDraw.clear(StdDraw.BLACK);
                    StdDraw.setPenColor(StdDraw.WHITE);
                    final int x = 40;
                    final int y =  25;
                    StdDraw.text(x, y, "Input the seed (press S when done):");
                    String str = "";
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                            if (c == 'S') {
                                Long seed = Long.parseLong(str);
                                world = new World(seed);
                                break;
                            } else if (Character.isDigit(c)) {
                                str += c;
                                StdDraw.clear(StdDraw.BLACK);
                                StdDraw.text(x, y, "Input the seed (press S when done):");
                                StdDraw.text(x, y, str);
                            }
                        }
                    }
                }
            }
        }
        StdDraw.show();
    }

    private void initialize() {
        ter.initialize(WIDTH, HEIGHT, 0, 0);
    }

    private long handleInput(String[] args) {
        long seed;
        try (Scanner scanner = new Scanner(System.in)) {
            if (args.length > 0) {
                seed = parseSeed(String.join("", args));
            } else {
                System.out.println("Enter seed (or press enter for a random seed):");
                String input = scanner.nextLine();
                seed = input.isEmpty() ? new Random().nextLong() : parseSeed("N" + input + "S");
            }
        }
        return seed;
    }

    private long parseSeed(String input) {
        String number = input.replaceAll("[^0-9]", "");
        return Long.parseLong(number);
    }

    private void run() {
        while (true) {
            renderWorld();
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped() && Character.toUpperCase(StdDraw.nextKeyTyped()) == 'Q') {
                            saveWorld();
                            System.exit(5);
                            break;
                        }
                    }
                } else {
                    handleKey(key);
                }
            }
        }
    }

    private void renderWorld() {
        String hudText = getHUDText();
        ter.renderFrame(world.getWorld(), hudText);
        StdDraw.show();
    }

    private String getHUDText() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        TETile tile = world.getTile(mouseX, mouseY);
        return tile == null ? "" : tile.description();
    }

    //handles key strokes
    private void handleKey(char key) {
        key = Character.toUpperCase(key);
        if (key == 'W') {
            world.updateAvatar(0, 1);
        } else if (key == 'S') {
            world.updateAvatar(0, -1);
        } else if (key == 'A') {
            world.updateAvatar(-1, 0);
        } else if (key == 'D') {
            world.updateAvatar(1, 0);
        } else if (key == 'L') {
            loadWorld();
        }
    }


    public void saveWorld() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./savedWorld.txt"))) {
            out.writeObject(world);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void loadWorld() {
        File file = new File("./savedWorld.txt");
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                world = (World) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(2);
            }
        } else {
            throw new Error("can't find file");
        }
    }

    //get menu to work, get loading and saving to work, get the getworldfrominput to work
    //then think about 3c
}
