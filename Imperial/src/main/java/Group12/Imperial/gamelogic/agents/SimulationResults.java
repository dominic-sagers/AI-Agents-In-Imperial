package Group12.Imperial.gamelogic.agents;

public class SimulationResults {
    
    public final int winnerIndex;
    public final int tickCount;
    public final long timeInMillies;

    public SimulationResults(int winnerIndex, int tickCount, long timeInMillies ){
        this.winnerIndex = winnerIndex;
        this.tickCount = tickCount;
        this.timeInMillies = timeInMillies;
    }
}
