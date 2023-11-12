package Group12.Imperial.launchers;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.Player.PlayerType;
import Group12.Imperial.gamelogic.actions.Investor;
import Group12.Imperial.gamelogic.agents.GameState;
import Group12.Imperial.gamelogic.agents.InvestorState;
import Group12.Imperial.gamelogic.agents.tneuralnet.CostFunction;
import Group12.Imperial.gamelogic.agents.tneuralnet.Initializer;
import Group12.Imperial.gamelogic.agents.tneuralnet.Layer;
import Group12.Imperial.gamelogic.agents.tneuralnet.NeuralNetwork;
import Group12.Imperial.gamelogic.agents.tneuralnet.Result;
import Group12.Imperial.gamelogic.agents.tneuralnet.math.Vec;
import Group12.Imperial.gamelogic.agents.tneuralnet.optimizer.GradientDescent;
import Group12.Imperial.gamelogic.gameboard.Bond;

import java.util.ArrayList;

import static Group12.Imperial.gamelogic.agents.tneuralnet.Activation.Leaky_ReLU;
import static Group12.Imperial.gamelogic.agents.tneuralnet.Activation.Sigmoid;

import java.util.Random;

public class DQNTrainer {

    private static final int EPISODES = 10000;
    private static double explorationValue = 0.5;
    private static final double explorationDecay = 0.001;
    private Random rand = new Random();

    private int choicesTaken = 0;
    private int correctChoice = 0;

    private NeuralNetwork network;
    private NeuralNetwork targetNetwork;

    public static void main(String[] args) throws InterruptedException {
        DQNTrainer trainer = new DQNTrainer();
        trainer.start();
        
    }

    public void start() throws InterruptedException {
        network = new NeuralNetwork.Builder(54)
                .addLayer(new Layer(60, Leaky_ReLU, 1.0))
                .addLayer(new Layer(55, Leaky_ReLU, 1.0))
                .addLayer(new Layer(48, Sigmoid, 1.0))
                .initWeights(new Initializer.HeUniform())
                .setCostFunction(new CostFunction.MSE())
                .setOptimizer(new GradientDescent(0.6))
                .create();

        targetNetwork = new NeuralNetwork.Builder(54)
                .addLayer(new Layer(60, Leaky_ReLU))
                .addLayer(new Layer(55, Leaky_ReLU))
                .addLayer(new Layer(48, Sigmoid))
                .initWeights(new Initializer.HeUniform())
                .setCostFunction(new CostFunction.MSE())
                .setOptimizer(new GradientDescent(0.6))
                .create();

        network.load();
        targetNetwork.load();
        PlayerType[] playerTypes = {PlayerType.RULE, PlayerType.RULE};
        int counter = 0;
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < EPISODES; i++) {
            Controller controller = new Controller(playerTypes.length, playerTypes, false, this);
            controller.startTrainingEpisode();
            if(i % 100 == 0) System.out.println("Episode " + i + " exploration value: " + explorationValue);
            explorationValue = explorationValue - explorationDecay;
            counter++;
            if(counter > 100) {
                targetNetwork.update(network);
                counter = 0;
            }
        }
        System.out.println("Accuracy: " + choicesTaken + " / " + correctChoice);
        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime));
        network.save();
    }

    public int[] makeInvestorChoice(ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCost, InvestorState investorState) throws InterruptedException {
        
        int[] output = new int[2];
        int choice = 0;
        if(rand.nextDouble() < explorationValue) {
            choice = rand.nextInt(0, 48);
        } else {
            Result result = network.evaluate(investorState.getData());
            choice = result.getOutput().indexOfLargestElement();
        }
        if(explorationValue < 0.0) choicesTaken++;
        boolean isBuyable = investorState.getData().getData()[choice] == 1.0 ? true : false;
        int reward = 0;
        if(isBuyable) {
            if(explorationValue < 0.0) correctChoice++;
            reward = 1;
        }
        Bond chosenBond = investorState.allBonds[choice];
        Nation[] nations = investorState.getGameState().getNations();
        int[] nationScores = new int[6];
        int maxNation = -1;
        int max = 0;
        for(int i = 0; i < nations.length; i++){
            nationScores[i] = nations[i].getPowerPoints();
            if(nationScores[i] > max) { 
                max = nationScores[i]; 
                maxNation = i;
            }
        }
        if(isBuyable && chosenBond.getNationName().index == maxNation){
            //System.out.println("Chose Something that made sense");
            reward += 1;
        }

        if(isBuyable) {
            for(int i = 0; i < possibleActions.size(); i++) {
                if(possibleActions.get(i).getNationName() == chosenBond.getNationName() && possibleActions.get(i).getValue() == chosenBond.getValue()) {
                    output[0] = i;
                }
            }
        } else {
            output[0] = -1;
        }


        GameState tmpGameState = GameState.getCloneGameState(investorState.getGameState());
        Investor.manualInvestorWasPassed(tmpGameState, possibleActions, associatedCost, output);
        InvestorState nextState = new Controller(tmpGameState, false, 2).getNextInvestorState();
        Vec maxFutureReward;
        if(nextState != null) {
            Result res = targetNetwork.evaluate(nextState.getData());
            maxFutureReward = res.getOutput();
            Vec newVec = maxFutureReward.mul(0.1).add(reward);
            //System.out.println(nextState.getData().toString());

            Result r = network.evaluate(investorState.getData(), newVec);
        } else {
            double[] x = new double[48];
            Vec v = new Vec(x);
            v.add(10);
            Result r = network.evaluate(investorState.getData(), v);
        }
        network.updateFromLearning();
        //System.out.println("Current Cost " + r.getCost());
        
        return output;
    }

    class Experience {
        public InvestorState state;
        public int action;
        public int reward;
        public InvestorState nextState;

        public Experience(InvestorState state, int action, int reward, InvestorState nextState) {
            this.state = state;
            this.action = action;
            this.reward = reward;
            this.nextState = nextState;
        }
    }

    class ExperienceReplay {
        
        Experience[] replay;
        int currentIndex = 0;

        Random rand = new Random();

        public ExperienceReplay(int size) {
            replay = new Experience[size];
            
        }

        public void add(Experience experience) {
            if(currentIndex + 1 >= replay.length){
                for(int i = 0; i < replay.length-1; i++){
                    replay[i] = replay[i+1];
                }
                replay[replay.length-1] = experience;
            } else {
                replay[currentIndex] = experience;
                currentIndex++;
            }
            
        }

        public Experience[] getSample(int size) {
            Experience[] output = new Experience[size];
            for(int i = 0; i < size; i++){
                int randNum = rand.nextInt(replay.length);
                output[i] = replay[randNum];
                
                for(int j = 0; j < output.length; j++){
                    if(j !=i && output[j] == output[i]){
                        output[i] = null;
                        i--;
                    }
                }
            }
            return output;
        }


    }
}
