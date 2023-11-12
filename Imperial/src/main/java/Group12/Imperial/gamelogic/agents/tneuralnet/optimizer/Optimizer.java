package Group12.Imperial.gamelogic.agents.tneuralnet.optimizer;

import Group12.Imperial.gamelogic.agents.tneuralnet.math.Matrix;
import Group12.Imperial.gamelogic.agents.tneuralnet.math.Vec;

public interface Optimizer {

    void updateWeights(Matrix weights, Matrix dCdW);

    Vec updateBias(Vec bias, Vec dCdB);

    Optimizer copy();

}
