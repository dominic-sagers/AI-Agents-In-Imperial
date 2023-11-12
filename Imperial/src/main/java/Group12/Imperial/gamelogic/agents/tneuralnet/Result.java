package Group12.Imperial.gamelogic.agents.tneuralnet;

import java.io.Serializable;

import Group12.Imperial.gamelogic.agents.tneuralnet.math.Vec;

/**
 * The outcome of an evaluation.
 * Will always contain the output data.
 * Might contain the cost.  // Fixme: Optional?
 */
public class Result implements Serializable {

    private final Vec output;
    private final Double cost;

    public Result(Vec output) {
        this.output = output;
        cost = null;
    }

    public Result(Vec output, double cost) {
        this.output = output;
        this.cost = cost;
    }

    public Vec getOutput() {
        return output;
    }

    public Double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Result{" + "output=" + output +
            ", cost=" + cost +
            '}';
    }
}
