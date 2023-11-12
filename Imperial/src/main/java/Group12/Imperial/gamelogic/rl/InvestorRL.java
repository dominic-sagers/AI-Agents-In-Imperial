// package Group12.Imperial.gamelogic.rl;

// import java.util.ArrayList;
// import java.util.Random;
// import java.util.*;

// import Group12.Imperial.gamelogic.actions.BondOptions;
// import Group12.Imperial.gamelogic.actions.Investor;
// import Group12.Imperial.gamelogic.agents.GameState;
// import Group12.Imperial.gamelogic.agents.InvestorState;
// import Group12.Imperial.gamelogic.gameboard.Bond;

// public class InvestorRL {
//     private final double learningRate = 0.1;
//     private final double discountFactor = 0.9;
//     private final double exploration = 0.1;
//     private Map<InvestorState,Double[]> hashValues;
//     private ArrayList<Tuple<InvestorState,Double[]>> values;
//     private ArrayList<Tuple<InvestorState,Integer>> rewards;
//     private Random rand;

//     public InvestorRL() {
//         rewards = new ArrayList<>();
//     }

//     public void updatestate(InvestorState state, int action, double reward){
//         Double[] options = hashValues.get(state);
//         double maxFutureValue = Double.NEGATIVE_INFINITY;

//         if (options.equals(null)){
//             Double[] newValues = new Double[state.bonds.length];
//             newValues[action] = learningRate * reward;
//             hashValues.put(state, newValues);
//         }
//         else{
//             // a part of the formula 
//             for (int i = 0; i < options.length; i++) {
//                 maxFutureValue = Math.max(options[i], maxFutureValue);
//             }
//             Double[] oldvalues = options;
//             //this is the update formula thing 
//             Double newvalue = oldvalues[action] + (learningRate * (reward + (discountFactor* maxFutureValue) - oldvalues[action]));
//             oldvalues[action] = newvalue;
//             hashValues.put(state, oldvalues);
//         }

//     }
//     public Bond chooseBond(InvestorState state) throws Exception{
//         //explorations stuff
//         if (Math.random() < exploration) {
//             return state.bonds[rand.nextInt(state.bonds.length)];
//         }
//         //get the gamestate
//         Double[] options = hashValues.get(state);
//         //never encountered this stuff
//         if (options == null) {
//             return state.bonds[rand.nextInt(state.bonds.length)];
//         }
//         double bestValue = Double.NEGATIVE_INFINITY;
//         Bond bestBond = null;
//         //get best value ( exploitation )
//         for (int i = 0; i < options.length; i++) {
//             if (options[i] > bestValue) {
//                 bestValue = options[i];
//                 bestBond = state.bonds[i];
//             }
//         }
//         //weird if this ever happens
//         if (bestBond.equals(null)) {
//             throw new Exception();
//         }
//         return bestBond;
//     }
    
//     public Bond makeDecisionBond(int playerMoney, GameState gameState) {
//         BondOptions bondOptions = Investor.getPossibleBonds(gameState.getController(), gameState.getPlayers()[gameState.getCurrentPlayer()]);
//         ArrayList<Bond> possibleBonds = bondOptions.possibleActions;
//         //InvestorState state = new InvestorState(possibleBonds);
//         return null;
//     }
        
// }