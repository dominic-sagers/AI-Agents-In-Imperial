package Group12.Imperial.gamelogic.agents.tneuralnet.math;

import java.io.Serializable;
import java.util.Random;

@SuppressWarnings("unused")
public class SharedRnd implements Serializable {

    private static Random rnd = new Random();

    public static Random getRnd() {
        return rnd;
    }

    public static void setRnd(Random rnd) {
        SharedRnd.rnd = rnd;
    }
}
