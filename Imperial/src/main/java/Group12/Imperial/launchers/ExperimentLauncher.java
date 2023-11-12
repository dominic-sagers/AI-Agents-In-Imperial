package Group12.Imperial.launchers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import Group12.Imperial.gamelogic.Player.PlayerType;
import Group12.Imperial.gamelogic.agents.Simulation;
import Group12.Imperial.gamelogic.agents.SimulationResults;

public class ExperimentLauncher {

    private final int ITERATIONS = 10;
    private final PlayerType[] playerTypes = {PlayerType.RULE, PlayerType.MCTS};
    private final long compLimitMCTS = 3000; 
    private final double explorationValue = 1.1;

    public void start() throws InterruptedException {

        experimentComputationalLimit();
        
    }

    private void experimentComputationalLimit() throws InterruptedException {
        SimulationResults[] results = new SimulationResults[ITERATIONS];
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());      
        for(int i = 0; i < ITERATIONS; i++) {
            int it = i;   
            threadPool.submit(() -> {
                System.out.println("Iteration " + it);
                try {
                    results[it] = Simulation.simulation(playerTypes, compLimitMCTS, explorationValue);
                } catch(InterruptedException e) {
                    results[it] = null;
                    System.out.println("Interrupted " + it);
                } catch(Exception e) {
                    results[it] = null;
                    e.printStackTrace();
                    System.out.println("Crashed " + it);
                } 
            });

        }

        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        try {
            FileWriter file = new FileWriter("Imperial/src/main/java/Group12/Imperial/experiments/CompLimit_" + compLimitMCTS + "-ExplorationValue_"+explorationValue + ".csv", false);
            PrintWriter writer = new PrintWriter(file);
            // tickCount,timeInMillies,winnerIndex
            for(int i = 0; i < results.length; i++) {
                if(results[i] != null) {
                    writer.println(results[i].tickCount + "," + results[i].timeInMillies + "," + results[i].winnerIndex);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void experimentComputationalLimitN() throws InterruptedException {
        SimulationResults[] results = new SimulationResults[ITERATIONS];
        for(int i = 0; i < ITERATIONS; i++) {
            int it = i;   
            
                System.out.println("Iteration " + it);
                try {
                    results[it] = Simulation.simulation(playerTypes, compLimitMCTS, explorationValue);
                } catch(InterruptedException e) {
                    results[it] = null;
                    e.printStackTrace();
                    System.out.println("Interrupted " + it);
                } catch(Exception e) {
                    results[it] = null;
                    e.printStackTrace();
                    System.out.println("Crashed " + it);
                } 
            

        }

        try {
            FileWriter file = new FileWriter("Imperial/src/main/java/Group12/Imperial/experiments/CompLimit_" + compLimitMCTS + "-ExplorationValue_"+explorationValue + ".csv", false);
            PrintWriter writer = new PrintWriter(file);
            // tickCount,timeInMillies,winnerIndex
            for(int i = 0; i < results.length; i++) {
                if(results[i] != null) {
                    writer.println(results[i].tickCount + "," + results[i].timeInMillies + "," + results[i].winnerIndex);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bugFixer() throws InterruptedException {
        for(int i = 0; i < ITERATIONS; i++) {
            System.out.println(i);
            Simulation.simulation(playerTypes, compLimitMCTS, explorationValue);
        }
        
    }

    public static void main(String[] args) throws InterruptedException {
        ExperimentLauncher launcher = new ExperimentLauncher();
        launcher.start();
    }
}
