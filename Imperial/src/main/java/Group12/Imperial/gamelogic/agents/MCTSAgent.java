package Group12.Imperial.gamelogic.agents;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.Player;
import Group12.Imperial.gamelogic.actions.Maneuver;
import Group12.Imperial.gamelogic.actions.Production;
import Group12.Imperial.gamelogic.actions.Taxation;
import Group12.Imperial.gamelogic.agents.tneuralnet.NeuralNetwork;
import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.gameboard.Unit;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.gameboard.map.Map;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation;
import Group12.Imperial.gui.board.rondel.RondelGUI.RondelChoice;
import Group12.Imperial.gamelogic.actions.BondOptions;
import Group12.Imperial.gamelogic.actions.Import;
import Group12.Imperial.gamelogic.actions.Investor;
import Group12.Imperial.gamelogic.agents.tneuralnet.CostFunction;
import Group12.Imperial.gamelogic.agents.tneuralnet.Initializer;
import Group12.Imperial.gamelogic.agents.tneuralnet.Layer;
import Group12.Imperial.gamelogic.agents.tneuralnet.Result;
import Group12.Imperial.gamelogic.agents.tneuralnet.math.Vec;
import Group12.Imperial.gamelogic.agents.tneuralnet.optimizer.GradientDescent;

import static Group12.Imperial.gamelogic.agents.tneuralnet.Activation.Leaky_ReLU;
import static Group12.Imperial.gamelogic.agents.tneuralnet.Activation.Sigmoid;

public class MCTSAgent implements AgentInterface, Serializable {
    private static int iterations = 1;
    private static int currentID = 0;
    private final int ID;
    private long limit = 1000;
    private double explorationValue;

    private NeuralNetwork network;
    private boolean withDQN = false;

    /*
     * TODO:
     * o MCTS agent is asked to make a move
     * o Based on the move, gather all possible initial choices, and then within the
     * resulting states of each choice, continually return the possibilities for
     * continued simulation
     * o Simulate and run algorithm based on these states
     * Selection:
     */
    private Node nextMove;

    public MCTSAgent(long limit, double explorationValue) {
        Arrays.stream(new File("Imperial/src/main/java/Group12/Imperial/testing/").listFiles()).forEach(File::delete);
        this.ID = currentID;
        this.limit = limit;
        this.explorationValue = explorationValue;
        currentID++;
    }

    public MCTSAgent(long limit, double explorationValue, boolean withDQN) {
        this.ID = currentID;
        this.limit = limit;
        this.explorationValue = explorationValue;
        this.withDQN = withDQN;
        currentID++;
        network = new NeuralNetwork.Builder(54)
                .addLayer(new Layer(60, Leaky_ReLU, 1.0))
                .addLayer(new Layer(55, Leaky_ReLU, 1.0))
                .addLayer(new Layer(48, Sigmoid, 1.0))
                .initWeights(new Initializer.HeUniform())
                .setCostFunction(new CostFunction.MSE())
                .setOptimizer(new GradientDescent(0.8))
                .create();
        network.load();
    }

    @Override
    public int makeRondelChoice(GameState state) throws InterruptedException {
        GameTree tree = new GameTree(GameState.getCloneGameState(state), explorationValue);
        int choice = MCTS(tree, false);
        
        if (state.getRondelNationPositions()[state.getCurrentNation()] != -1) {
            state.getPlayers()[state.getCurrentPlayer()].reduceMoney(getRondelCost(state, choice));
        }

        tree = null;
        return choice;
    }

    @Override
    public ArrayList<ArrayList<Integer>> makeManeuverChoice(GameState state) {
        // ArrayList<ArrayList<Integer>> moves = nextMove.getManeuverMade();
        // for(ArrayList<Integer> move : moves) {
        //     // {{strength, unitType, fromLocation,toLocation}}
        //     Maneuver.moveUnit(state.getController(), state.getMap(), state.getCurrentNation(), move.get(2), move.get(3), (move.get(1).intValue() == 0 ? UnitType.ARMY : UnitType.SHIP), move.get(0));
        // }

        // return nextMove.getManeuverMade();//#endregion
        return Rules.manueuverRule(state);
    }

    @Override
    public int makeFactoryChoice(GameState state) {
        return nextMove.getFactoryChoiceMade();
    }

    @Override
    public ArrayList<ArrayList<Integer>> makeImportChoice(GameState state) {
        //[locationIndex, unitType]
        ArrayList<ArrayList<Integer>> importChoices = nextMove.getImportChoiceMade();
        
        for(ArrayList<Integer> choice : importChoices){
            Nation nation = state.getNations()[state.getCurrentNation()];
            Unit unit = new Unit((choice.get(1).intValue() == 0 ? UnitType.SHIP : UnitType.ARMY), nation, 1, choice.get(0));
            state.getMap().getLocation(choice.get(0)).addUnit(nation.getIndex(), unit);
            nation.addUnitLocation(choice.get(0));
            nation.reduceMoney(1);
        }


        return nextMove.getImportChoiceMade();
    }

    @Override
    public int[] makeBondBuyChoice(GameState state, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCosts) {//IF WE DO MAKE MCTS, SET THE BONDS BOUGHT HERE AS WELL
        if(withDQN) {
            InvestorState invState = new InvestorState(state);
            Result result = network.evaluate(invState.getData());
            int choice = result.getOutput().indexOfLargestElement();
            boolean isBuyable = invState.getData().getData()[choice] == 1.0 ? true : false;
            Bond chosenBond = invState.allBonds[choice];
            int[] output = new int[2];
            System.out.println(isBuyable + ", choice: " + chosenBond.getNationName() + ", " + chosenBond.getValue());
            if(isBuyable) {
                for(int i = 0; i < possibleActions.size(); i++) {
                    if(possibleActions.get(i).getNationName() == chosenBond.getNationName() && possibleActions.get(i).getValue() == chosenBond.getValue()) {
                        output[0] = i;
                    }
                }
            } else {
                output[0] = -1;
            }
            return output;
        } else {
            return RandomChoice.makeInvestorChoice(GameState.getCloneGameState(state), possibleActions, associatedCosts);
        }
    }

    @Override
    public int makeBattleChoice(GameState state, int nation, int battleLocation, ArrayList<Integer> possibleBattles) {// UNUSED. EITHER ALWAYS PASSIVE OR ALWAYS AGGRESSIVE
        Random randomObj = new Random();                                                                                                  
        if(randomObj.nextBoolean()){
            return 0;
        } else {
            int j = possibleBattles.indexOf(nation);
            if(j != -1) possibleBattles.remove(j);
            if(possibleBattles.size() == 1) return possibleBattles.get(0)+1;
            int choice = possibleBattles.get(RandomChoice.randomWithinRange(0, possibleBattles.size()-1))+1;
            return choice;
        }
    }

    @Override
    public boolean makeHostileChoice(GameState state, int locationIndex, int nationIndex, UnitType unitType) {// UNUSED. EITHER ALWAYS PASSIVE OR ALWAYS AGGRESSIVE
        return true; // True = angry mode, False = pacifist mode
    }

    public int MCTSMultiThreaded(GameTree tree, boolean isInvestor) throws InterruptedException {
        /*
         * 1) Start: Expand from root (gather all options from current state)
         * 2) Simulate all un-visited choices from root state.
         * 3) Select the most promising node, or randomly from the nodes with equal
         * probability.
         * 4) Simulate all of this node's children, apply their values, update the
         * node's statistics
         * 5) Start from the top of the tree, apply exlpoitation/exploration weights,
         * and repeat selection expansion process
         */

        // populate the root with all starting states
        
        Node root = tree.getRoot();

        GameState rootState = root.getState();
        if(!isInvestor) {
            ArrayList<Integer> rondelOptions = getPossibleRondelChoices(rootState);// gets all rondel options
            for (Integer option : rondelOptions) {// Adds every resulting state from every reachable rondel option to the children of the root
                ArrayList<Node> choicePossibilities = getOptions(rootState, option.intValue(), root);
                for (Node node : choicePossibilities) {
                    tree.addNode(node);
                }
               
            }
            
        } else {
            ArrayList<Node> choicePossibilities = getOptionsInvestor(rootState, root);
                for (Node resultingState : choicePossibilities) {
                    
                    tree.addNode(resultingState);
                }
        }
        long startTime = System.nanoTime();
        // START OF MCTS
        Node nextNode;
        while (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) < limit) {// While algorithm has run under set amount of time, run
            
            //// Expansion
            if (!tree.allNodesExplored()) {
                ArrayList<Node> unexploredNodes = tree.getUnexploredNodes();
                long startOfExecution = System.nanoTime();
                ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                double[] results = new double[unexploredNodes.size()];
                for (int i = 0; i < unexploredNodes.size(); i++) {
                    int threadindex = i;
                    int upperBound = unexploredNodes.size();
                    Node nodeToExplore = unexploredNodes.get(i);
                    threadPool.submit(() -> {
                        GameState nodeState = GameState.getCloneGameState(nodeToExplore.getState());
                        int[] scores;
                        try {
                            scores = Simulation.randomRollOut(nodeState, nodeState.getCurrentPlayer());
                        } catch (InterruptedException e) {
                            scores = null;
                            e.printStackTrace();
                        }catch (Exception e) {
                            scores = null;
                            e.printStackTrace();
                        }
                        int maxScore = -1;
                        int maxIndex = -1;
                        for(int j = 0 ; j < scores.length; j++) {
                            if(scores[j] > maxScore){
                                maxScore = scores[j]; 
                                maxIndex = j;
                            }
                        }
                        double result;
                        if(maxIndex == rootState.getCurrentPlayer()){
                            result = 1;
                        }else{
                            result = 0;
                        }
                        results[threadindex] = result;
                    });
                }  
                
                threadPool.shutdown();
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                
                for (int i = 0; i < unexploredNodes.size(); i++) {
                    unexploredNodes.get(i).backPropogate(unexploredNodes.get(i), results[i]);
                    tree.applyUCT();
                    nextMove = tree.getNextBestMove();
                }
            }
            nextMove = tree.getNextBestMove();
            /////// Selection
            tree.applyUCT();
            nextNode = tree.getNextBestNode();
            ArrayList<Integer> nextNodeRondelOptions = getPossibleRondelChoices(nextNode.getState());
            int amt = 0;                                                                                    
            for (int option : nextNodeRondelOptions) {
                ArrayList<Node> choicePossibilities = getOptions(nextNode.getState(), option, nextNode);
                amt = amt + choicePossibilities.size();
                for (Node node : choicePossibilities) {
                    tree.addNode(node);
                }
            }
        }
        Node maxNode = tree.getNextBestMove();
        nextMove = maxNode;
        // EXIT OF MCTS
        
        //boolean debug = true;
        //if(debug){
        
        // FileWriter file;
        // PrintWriter writer;

        // try {
        //     System.out.println("Here");
        //     file = new FileWriter("Imperial/src/main/java/Group12/Imperial/testing/MCTS-ID" + ID + "-Iteration-" + MCTSAgent.iterations + ".txt", true);
        //     writer = new PrintWriter(file);
        //     writer.println("Time limit: " + limit);
        //     writer.println("");
        //     writer.println("Max node is at depth: " + maxNode.getDepth());
        //     writer.println("We chose move " + maxNode.getRondelChoice());
        //     writer.println("");
            

        //     writer.printf("------------------\nmaxNode has parameters:\n maneuverMade: %s\nfactoryChoiceMade: %d\nimportChoiceMade: %s\nbondChoiceMade: %s\n------------------\n", maxNode.getManeuverMade(), 
        //     maxNode.getFactoryChoiceMade(), maxNode.getImportChoiceMade(), maxNode.getBondChoiceMade());
        //     writer.println("--------------------------------Tree:------------------------------------ \n");
            
        //     tree.printTreeWriter(writer);

        //     writer.println("Tree depth: " + tree.getMaxDepth());

        //     writer.close();

        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        //}    
        MCTSAgent.iterations++;

   
        return maxNode.getRondelChoice();// return rondel choice from best state
    }


    public int MCTS(GameTree tree, boolean isInvestor) throws InterruptedException {
        Node root = tree.getRoot();
        GameState rootState = root.getState();

        if(!isInvestor) {
            ArrayList<Integer> rondelOptions = getPossibleRondelChoices(rootState);// gets all rondel options
            
            for (Integer option : rondelOptions) {// Adds every resulting state from every reachable rondel option to the children of the root
                ArrayList<Node> choicePossibilities = getOptions(rootState, option.intValue(), root);
                for (Node node : choicePossibilities) {
                    tree.addNode(node);
                }
               
            }
            
        } else {
            ArrayList<Node> choicePossibilities = getOptionsInvestor(rootState, root);
                for (Node resultingState : choicePossibilities) {
                    
                    tree.addNode(resultingState);
                }
        }
        long startTime = System.nanoTime();
        // START OF MCTS
        Node nextNode;
        while (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) < limit) {// While algorithm has run under set amount of time, run
            
            //// Expansion
            if (!tree.allNodesExplored()) {
                ArrayList<Node> unexploredNodes = tree.getUnexploredNodes();
                long startOfExecution = System.nanoTime();
                double[] results = new double[unexploredNodes.size()];
                for (int i = 0; i < unexploredNodes.size(); i++) {
                    int threadindex = i;
                    int upperBound = unexploredNodes.size();
                    Node nodeToExplore = unexploredNodes.get(i);
                    GameState nodeState = GameState.getCloneGameState(nodeToExplore.getState());
                    int[] scores;
                    
                    scores = Simulation.randomRollOut(nodeState, nodeState.getCurrentPlayer());
                    
                    int maxScore = -1;
                    int maxIndex = -1;
                    for(int j = 0 ; j < scores.length; j++) {
                        if(scores[j] > maxScore){
                            maxScore = scores[j]; 
                            maxIndex = j;
                        }
                    }
                    double result;
                    if(maxIndex == rootState.getCurrentPlayer()){
                        result = 1;
                    }else{
                        result = 0;
                    }
                    results[threadindex] = result;
                }  
                
                for (int i = 0; i < unexploredNodes.size(); i++) {
                    unexploredNodes.get(i).backPropogate(unexploredNodes.get(i), results[i]);
                    tree.applyUCT();
                    nextMove = tree.getNextBestMove();
                }
            }
            nextMove = tree.getNextBestMove();
            /////// Selection
            tree.applyUCT();
            nextNode = tree.getNextBestNode();
            
            ArrayList<Integer> nextNodeRondelOptions = getPossibleRondelChoices(nextNode.getState());
            int amt = 0;                                                                                    
            for (int option : nextNodeRondelOptions) {
                ArrayList<Node> choicePossibilities = getOptions(nextNode.getState(), option, nextNode);

                amt = amt + choicePossibilities.size();
                for (Node node : choicePossibilities) {
                    tree.addNode(node);
                }
            }
        }
        Node maxNode = tree.getNextBestMove();
        nextMove = maxNode;

        FileWriter file;
        PrintWriter writer;

        try {
            //System.out.println("Here");
            file = new FileWriter("Imperial/src/main/java/Group12/Imperial/testing/MCTS-ID" + ID + "-Iteration-" + MCTSAgent.iterations + ".txt", true);
            writer = new PrintWriter(file);
            writer.println("Time limit: " + limit);
            writer.println("");
            writer.println("Max node is at depth: " + maxNode.getDepth());
            writer.println("We chose move " + maxNode.getRondelChoice());
            writer.println("");
            

            writer.printf("------------------\nmaxNode has parameters:\n maneuverMade: %s\nfactoryChoiceMade: %d\nimportChoiceMade: %s\nbondChoiceMade: %s\n------------------\n", maxNode.getManeuverMade(), 
            maxNode.getFactoryChoiceMade(), maxNode.getImportChoiceMade(), maxNode.getBondChoiceMade());
            writer.println("--------------------------------Tree:------------------------------------ \n");
            
            tree.printTreeWriter(writer);

            writer.println("Tree depth: " + tree.getMaxDepth());

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        MCTSAgent.iterations++;
        //if(maxNode.getRondelChoice()==0 || maxNode.getRondelChoice()==4) System.out.println("Maneuver Chosen");
        return maxNode.getRondelChoice();// return rondel
                                                                                                    // choice from best
                                                                                                    // state
    }

    // returns an arraylist of nodes containing all possibilities from each rondel
    // action in the current gamestate.
    public ArrayList<Node> getOptions(GameState initialState, int rondelChoice, Node parent) throws InterruptedException {
        GameState state = GameState.getCloneGameState(initialState);
        RondelChoice choice = state.getRondel()[rondelChoice];// reduce money at each possible choice
        if (state.getRondelNationPositions()[state.getCurrentNation()] != -1) {
            state.getPlayers()[state.getCurrentPlayer()].reduceMoney(getRondelCost(state, rondelChoice));// reducing the appropriate amount of money based on rondelchoice
        }
        state.getRondelNationPositions()[state.getCurrentNation()] = rondelChoice;
        

        ArrayList<Node> options = new ArrayList<>();

        switch (choice) {
            case FACTORY:// Mostly code from RandomChoice
                Nation currentNation = state.getNations()[state.getCurrentNation()];// grabbing currentNation from the
                // gamestate.
                if (currentNation.getTreasury() - 5 < 0) {// returns -1 if we cannot afford a factory
                    
                    return options;
                }
                int[] homeProvincesOfNation = currentNation.getHomeProvinces();
                Map map = state.getMap();

                for (int i = 0; i < homeProvincesOfNation.length; i++) {
                    MapLocation currentLocation = map.getLocation(homeProvincesOfNation[i]);
                    if (currentLocation.getFactory() == null) {  
                        if (!currentLocation.hasHostileUnitOfOtherNation(currentNation.getIndex())) {
                            Node factoryNode = new Node(parent, GameState.getCloneGameState(state));
                            factoryNode.setRondelChoice(rondelChoice);
                            factoryNode.setMadeMoveFactoryChoice(currentLocation.getIndex());// Sets the location index for use in
                            factoryNode.getState().getController().buildFactory(currentLocation.getIndex(), currentNation.getIndex());// build a factory in the node's gamestate to refle the outcome
                            factoryNode.getState().getNations()[currentNation.getIndex()].reduceMoney(5);
                            factoryNode.getState().incrementTurn();
                            options.add(factoryNode);
                        }

                    }
                }
                break;
            case IMPORT:
                GameState newStateImport = GameState.getCloneGameState(state);
                int maxAmountOfUnits = getImportLimit(newStateImport);
                ArrayList<Integer> validLocations = new ArrayList<>();
                for(MapLocation location : newStateImport.getMap().getAllMapLocations()){
                    if(location.isHomeProvince() && location.getOwner().getIndex() == newStateImport.getCurrentNation()){
                        boolean importIsLegal = true;
                        for(int nationIndex : location.getNationsPresent()){
                            if(nationIndex != newStateImport.getCurrentNation() && location.getUnit(nationIndex, UnitType.ARMY).isHostile()){
                                importIsLegal = false;
                            }
                        }
                        if(importIsLegal){
                            validLocations.add(location.getIndex());
                        }
                    }
                }
                
                for(int i = 1; i < maxAmountOfUnits+1; i++) { // 1 , 2 , 3
                    
                    int randomValidLocation = validLocations.get(RandomChoice.getRandomListChoice(validLocations));
                    
                    boolean shipLegal = Import.isImportLegalBoolean(newStateImport.getController(), newStateImport.getCurrentNation(), newStateImport.getMap(), randomValidLocation, UnitType.SHIP); 
                    
                    if(shipLegal) {
                        
                        Node shipNode = new Node(parent, GameState.getCloneGameState(newStateImport));
                        shipNode.setRondelChoice(rondelChoice);
                        newStateImport.getNations()[newStateImport.getCurrentNation()].reduceMoney(1);
                        ArrayList<ArrayList<Integer>> choicesShip = new ArrayList<ArrayList<Integer>>();
                        for (int index = 0; index < i; index++) {
                            ArrayList<Integer> choiceShip = new ArrayList<>();
                            choiceShip.add(randomValidLocation);
                            choiceShip.add(1);
                            choicesShip.add(choiceShip);
                            Unit unit = new Unit(UnitType.SHIP, shipNode.getState().getNations()[shipNode.getState().getCurrentNation()], 1, randomValidLocation);
                            shipNode.getState().getMap().getLocation(randomValidLocation).addUnit(shipNode.getState().getCurrentNation(), unit);
                            shipNode.getState().getNations()[shipNode.getState().getCurrentNation()].addUnitLocation(randomValidLocation);
                            shipNode.getState().getNations()[shipNode.getState().getCurrentNation()].reduceMoney(1);
                        }
                        
                        shipNode.setMadeMoveImportChoice(choicesShip);
                        shipNode.getState().incrementTurn();
                        options.add(shipNode);

                    }
                    
                    Node armyNode = new Node(parent, GameState.getCloneGameState(newStateImport));
                    armyNode.setRondelChoice(rondelChoice);
                    newStateImport.getNations()[newStateImport.getCurrentNation()].reduceMoney(1);
                    ArrayList<ArrayList<Integer>> choicesArmy = new ArrayList<ArrayList<Integer>>();
                    for(int j = 0; j < i; j++) {
                        ArrayList<Integer> choiceArmy = new ArrayList<Integer>();
                        choiceArmy.add(randomValidLocation);
                        choiceArmy.add(0);
                        choicesArmy.add(choiceArmy);
                        Unit unit = new Unit(UnitType.SHIP, armyNode.getState().getNations()[armyNode.getState().getCurrentNation()], 1, randomValidLocation);
                        armyNode.getState().getMap().getLocation(randomValidLocation).addUnit(armyNode.getState().getCurrentNation(), unit);
                        armyNode.getState().getNations()[armyNode.getState().getCurrentNation()].addUnitLocation(randomValidLocation);
                        armyNode.getState().getNations()[armyNode.getState().getCurrentNation()].reduceMoney(1);
                    }
                    
                    armyNode.setMadeMoveImportChoice(choicesArmy);
                    armyNode.getState().incrementTurn();
                    options.add(armyNode);
                }
                
                break;
            case MANEUVER:
                ArrayList<ArrayList<Integer>> validManeuver = state.getValidManeuvers();
                List<int[]> combinations = generateAll(validManeuver.size());
                
                //for (int[] combination : combinations) {

                //     ArrayList<ArrayList<Integer>> fromLocations = new ArrayList<>();
                //     for (int i : combination) {
                //         fromLocations.add(validManeuver.get(i));

                //     }
                
                //     ArrayList<ArrayList<Integer>> chosenMoves = chooseToLocations(fromLocations);
                //     GameState newState = GameState.getCloneGameState(state);
                //     // {{strength, unitType, fromLocation, toLocation}}
                //     for (int i = 0; i < chosenMoves.size(); i++) {
                //         int from = chosenMoves.get(i).get(2);
                //         int to = chosenMoves.get(i).get(3);
                //         UnitType unitType;
                //         if (chosenMoves.get(i).get(1) == 1) {
                //             unitType = UnitType.SHIP;
                //         } else {
                //             unitType = UnitType.ARMY;
                //         }
                //         int amount = chosenMoves.get(i).get(0);
                //         if(!(newState.getMap().getLocation(from).getUnits()[newState.getCurrentNation()] == null && newState.getMap().getLocation(from).getUnits()[newState.getCurrentNation()+6] == null)) {
                //             Maneuver.moveUnit(newState.getController(), newState.getMap(), newState.getCurrentNation(), from, to, unitType, amount);
                //         }
                //     }

                //     Node newNode = new Node(parent, newState);
                //     newNode.setRondelChoice(rondelChoice);
                //     newNode.setMadeMoveManeuver(chosenMoves);
                //     newNode.getState().incrementTurn();
                //     options.add(newNode);
                // }
                Node newNodeM = new Node(parent, state);
                ArrayList<ArrayList<Integer>> ruleBasedManeuver = Rules.manueuverRule(state);
                newNodeM.setRondelChoice(rondelChoice);
                newNodeM.setMadeMoveManeuver(ruleBasedManeuver);
                newNodeM.getState().incrementTurn();
                options.add(newNodeM);
                break;
            case INVESTOR:
                GameState newState = GameState.getCloneGameState(state);
                Investor.makeInvestorChoice(newState.getController(), newState.getPlayers(), newState.getNations());
                Node newNode = new Node(parent, newState);
                newNode.setRondelChoice(rondelChoice);
                newNode.getState().incrementTurn();
                options.add(newNode);
                break;
            case PRODUCTION:
                GameState newStateProd = GameState.getCloneGameState(state);
                
                Production.produce(newStateProd.getMap(), newStateProd.getNations()[newStateProd.getCurrentNation()]);
                Node newNodeProd = new Node(parent, newStateProd);
                newNodeProd.setRondelChoice(rondelChoice);
                newNodeProd.getState().incrementTurn();
                options.add(newNodeProd);
                break;
            case TAXATION:
                GameState newStateTaxation = GameState.getCloneGameState(state);
                
                Taxation.taxation(newStateTaxation.getController(), newStateTaxation.getMap(),
                        newStateTaxation.getNations()[newStateTaxation.getCurrentNation()],
                        newStateTaxation.getTaxChart());
                Node newNodeTax = new Node(parent, newStateTaxation);
                newNodeTax.setRondelChoice(rondelChoice);
                newNodeTax.getState().incrementTurn();
                options.add(newNodeTax);
                break;
        }
        
        return options;// return all outcomes as nodes with resulting game states based on their rondel
                       // choice.
    }

    public ArrayList<Node> getOptionsInvestor(GameState state, Node parent){
        Player currentPlayer = state.getPlayers()[state.getCurrentPlayer()];
        currentPlayer.addMoney(2);
        ArrayList<Node> options = new ArrayList<Node>();
        BondOptions bondOptions = Investor.getPossibleBonds(state.getController(), currentPlayer);
        ArrayList<Bond> possibleBonds = bondOptions.possibleActions;
        options.add(new Node(parent, GameState.getCloneGameState(state)));//Didnt buy anything
        
        
        for(int j = 0; j < possibleBonds.size(); j++) {
            ArrayList<Integer> buyOptions = Investor.getBondBuyOptions(possibleBonds.get(j), currentPlayer.getOwnedBonds(), currentPlayer.getMoney());
            for(int i = 0 ;  i < buyOptions.size(); i = i + 2) {
                Node resultingState = new Node(parent, GameState.getCloneGameState(state));
                Player newStatePlayer = resultingState.getState().getPlayers()[resultingState.getState().getCurrentPlayer()];
                Bond[] bondToBuyArray = resultingState.getState().getBondArrayOnName(possibleBonds.get(j).getNationName());
                Bond bondToBuy = null;
                int[] bondBuyChoice = new int[2];

                for(Bond bToB : bondToBuyArray) if(bToB.getValue() == possibleBonds.get(j).getValue()) bondToBuy = bToB;

                

                if(buyOptions.get(i) == -1) {
                    newStatePlayer.reduceMoney(buyOptions.get(i+1));
                    newStatePlayer.addBond(bondToBuy);
                    bondToBuy.setPlayer(newStatePlayer);
                    bondBuyChoice[0] = -1;
                    bondBuyChoice[1] = -1;
                } else {
                    Bond[] bondToUpgradeArray = resultingState.getState().getBondArrayOnName(possibleBonds.get(buyOptions.get(i)).getNationName());
                    Bond bondToUpgrade = null;
                    for(Bond bToU : bondToUpgradeArray) if(bToU.getValue() == possibleBonds.get(buyOptions.get(i)).getValue()) bondToUpgrade = bToU;

                    newStatePlayer.reduceMoney(buyOptions.get(i+1));
                    newStatePlayer.addBond(bondToBuy);
                    newStatePlayer.removeBond(bondToUpgrade);
                    bondToBuy.setPlayer(newStatePlayer);
                    bondBuyChoice[0] = j;
                    bondBuyChoice[0] = i;
                }
                
                resultingState.setMadeMoveBondBuyChoice(bondBuyChoice);
                options.add(resultingState);
            }
        }

        return options;
    }

    public int getRondelCost(GameState initialState, int rondelChoice) {
        int currentPosition = initialState.getRondelNationPositions()[initialState.getCurrentNation()];
        if (currentPosition == -1)
            return 0;

        for (int i = 1; i <= 6; i++) {
            currentPosition++;
            if (currentPosition > 7)
                currentPosition = 0;
            if (currentPosition == rondelChoice) {
                if (i < 4) {  
                    return 0;
                } else {
                    int extraStepsTaken = i - 3;
                    return extraStepsTaken*2;
                }
            }
        }
        return 0;
    }

    // {[fromlocation1={tolocations...}],
    // [fromlocation2={tolocations...}],[fromlocation2={tolocations...}],[fromlocation2={tolocations...}]}
    // "{strength, unitType, fromLocation,toLocation1,toLocation2,
    // toLocation3,....}".
    public ArrayList<ArrayList<Integer>> groupFromLocations(ArrayList<ArrayList<Integer>> possibleManeuvers) {
        ArrayList<ArrayList<Integer>> froms = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < possibleManeuvers.size(); i++) {
            ArrayList<Integer> from = new ArrayList<>();

            from.add(possibleManeuvers.get(i).get(3));

            for (int j = 0; j < possibleManeuvers.size(); j++) {
                if (possibleManeuvers.get(i) != possibleManeuvers.get(j)
                        && possibleManeuvers.get(i).get(2) == possibleManeuvers.get(j).get(2)) {
                    from.add(possibleManeuvers.get(j).get(3));
                }
            }
            froms.add(possibleManeuvers.get(i).get(2), from);
        }
        return froms;
    }

    public static ArrayList<Integer> getPossibleRondelChoices(GameState state) {
        Player player = state.getPlayers()[state.getCurrentPlayer()];
        ArrayList<Integer> choices = new ArrayList<>();
        int rondelIndex = state.getRondelNationPositions()[state.getCurrentNation()];
        int playerMoney = player.getMoney();
        int maxMoves = 0;
        if (rondelIndex == -1) {
            for (int i = 0; i < 8; i++) {
                choices.add(i);
            }
        } else {

            if (playerMoney < 2) {
                maxMoves = 3;
            } else if (playerMoney < 4) {
                maxMoves = 4;
            } else if (playerMoney < 6) {
                maxMoves = 5;
            } else if(playerMoney >= 6){
                maxMoves = 6;
            }

            for (int i = 1; i <= maxMoves; i++) {
                if (rondelIndex + i < 8) {
                    choices.add(rondelIndex + i);
                } else {
                    choices.add(rondelIndex + i - 8);
                } 
                // rondelIndex = 6, i = 4;  6+4-7
            }

        }

        return choices;
    }

    // Returns list of lists containing in every index all split up valid maneuvers
    // as {strength, unitType, fromLocation,toLocation}
    public static ArrayList<ArrayList<Integer>> getPossibleManeuverChoices(GameState state) {
        ArrayList<ArrayList<Integer>> validManeuvers = state.getValidManeuvers(); // "{strength, unitType,
                                                                                  // fromLocation,toLocation1,toLocation2,
                                                                                  // toLocation3,....}".
        ArrayList<ArrayList<Integer>> choices = new ArrayList<>();
        for (int i = 0; i < validManeuvers.size(); i++) {// Loops through all of the validManeuver list and splits each
                                                         // list into just all possible {strength, unitType,
                                                         // fromLocation,toLocation} permutations
            ArrayList<Integer> n = validManeuvers.get(i);
            if (n.size() < 4) {// if there is only one possible to-location
                choices.add(n);
                // ArrayList<Integer> zeroMove = n;
                // zeroMove.remove(3);
                // choices.add(zeroMove);
            } else {
                for (int j = 3; j < n.size(); j++) {// more than one to-location
                    ArrayList<Integer> newChoice = new ArrayList<>();
                    newChoice.add(n.get(0));
                    newChoice.add(n.get(1));
                    newChoice.add(n.get(2));
                    newChoice.add(n.get(j));

                    choices.add(newChoice);

                    // ArrayList<Integer> zeroMove = n;
                    // zeroMove.remove(3);
                    // choices.add(zeroMove);
                }
            }
        }
        return choices;
    }

    private static void helper(List<int[]> combinations, int data[], int start, int end, int index) {
        if (index == data.length) {
            int[] combination = data.clone();
            combinations.add(combination);
        } else if (start <= end) {
            data[index] = start;
            helper(combinations, data, start + 1, end, index + 1);
            helper(combinations, data, start + 1, end, index);
        }
    }

    public static List<int[]> generate(int n, int r) {
        List<int[]> combinations = new ArrayList<>();
        helper(combinations, new int[r], 0, n - 1, 0);
        return combinations;
    }

    public static List<int[]> generateAll(int n) {
        List<int[]> combinations = new ArrayList<>();
        for (int r = 1; r <= n; r++) {
            combinations.addAll(generate(n, r));
        }
        return combinations;
    }

    // {strength, unitType, fromLocation, toLocation1,toLocation2, toLocation3,....}
    public ArrayList<ArrayList<Integer>> chooseToLocations(ArrayList<ArrayList<Integer>> fromLocations) {
        ArrayList<ArrayList<Integer>> truncatedFromLocations = new ArrayList<>();
        for (ArrayList<Integer> fromLoc : fromLocations) {

            if (fromLoc.size() >= 5) {
                // Random hueristic
                int choice = RandomChoice.randomWithinRange(3, fromLoc.size() - 1);
                ArrayList<Integer> newFrom = new ArrayList<>();
                newFrom.add(fromLoc.get(0));
                newFrom.add(fromLoc.get(1));
                newFrom.add(fromLoc.get(2));
                newFrom.add(fromLoc.get(choice));
                truncatedFromLocations.add(newFrom);

            } else if(fromLoc.size()==4){
                truncatedFromLocations.add(fromLoc);
            }

        }
        return truncatedFromLocations;
    }

    public int getImportLimit(GameState state){
        int limit=0;
        if(state.getNations()[state.getCurrentNation()].getTreasury() -3 >= 0){
            limit = 3;
        }else if(state.getNations()[state.getCurrentNation()].getTreasury() -2 >= 0){
            limit = 2;
        }else if(state.getNations()[state.getCurrentNation()].getTreasury() -1 >= 0){
            limit = 1;
        }
        return limit;
    }


}
