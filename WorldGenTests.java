import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

import java.util.Arrays;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789i");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles, "");
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        TETile[][] t1 = AutograderBuddy.getWorldFromInput("N999SDDDWWWDDD");
        TETile[][] t2 = AutograderBuddy.getWorldFromInput("N999SDDD:Q");
        t2 = AutograderBuddy.getWorldFromInput("LWWWDDD");
        TETile[][] t3 = AutograderBuddy.getWorldFromInput("N999SDDD:Q");
        t3 = AutograderBuddy.getWorldFromInput("LWWW:Q");
        t3 = AutograderBuddy.getWorldFromInput("LDDD:Q");


        TERenderer ter  = new TERenderer();

        ter.renderFrame(t1,"");
        //ter.renderFrame(t2,"");
        System.out.println(Arrays.deepEquals(t1,t2));
        System.out.println(Arrays.deepEquals(t1,t3));
        System.out.println(Arrays.deepEquals(t2,t3));
    }

    @Test
    public void basicSaveTest() {
        // TODO: write a test that calls getWorldFromInput twice, with "n123swasd:q" and with "lwasd"
    }
}
