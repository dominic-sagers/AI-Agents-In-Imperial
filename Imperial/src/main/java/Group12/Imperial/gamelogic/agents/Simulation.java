package Group12.Imperial.gamelogic.agents;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Player.PlayerType;

public class Simulation {

    public static int[] randomRollOut(GameState initialState, int startingNation) throws InterruptedException {
        Controller controller = new Controller(initialState, false, 1);
        int[] playerScores = controller.start();
        return playerScores;
    }
    //arraylist <<results>, <tickcount>, <time>>
    public static SimulationResults simulation(PlayerType[] playerTypes, long compLimitMCTS, double explorationValue) throws InterruptedException {
        int playerCount = playerTypes.length;
        Controller controller = new Controller(null, playerCount, playerTypes, false);
        controller.compLimitMCTS = compLimitMCTS;
        controller.explorationValue = explorationValue;
        int[] playerScores = controller.start();
        int winnerIndex = -1;
        int winnerScore = -1;
        for(int i = 0; i < playerScores.length; i++) {
            if(playerScores[i]> winnerScore){
                winnerScore = playerScores[i];
                winnerIndex = i;
            }
        }

        return new SimulationResults(winnerIndex, controller.tickCount, controller.timeTook);
    }
    
}
