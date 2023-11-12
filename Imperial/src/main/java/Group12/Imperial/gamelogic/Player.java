package Group12.Imperial.gamelogic;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.agents.AgentInterface;
import Group12.Imperial.gamelogic.agents.MCTSAgent;
import Group12.Imperial.gamelogic.agents.RandomAgent;
import Group12.Imperial.gamelogic.agents.RuleAgent;
import Group12.Imperial.gamelogic.gameboard.Bond;

public class Player implements Serializable{

    public enum PlayerType implements Serializable{
        HUMAN, RANDOM, MCTS, MCTSDQN, RULE
    }

    private PlayerType playerType;
    private int index;
    private ArrayList<Nation> ownedNations;
    private ArrayList<Bond> ownedBonds;
    private int money;
    private boolean hasInvestor;

    private AgentInterface agent;

    public Player(int index, int money, ArrayList<Nation> ownedNations, ArrayList<Bond> ownedBonds, PlayerType playerType){
        this.index = index;
        this.money = money;
        this.ownedNations = ownedNations;
        this.ownedBonds = ownedBonds;
        this.playerType = playerType;
    }

    public Player(int index, int money, PlayerType playerType){
        this.index = index;
        this.money = money;
        this.playerType = playerType;
    }

    public PlayerType getType() { return playerType; }
    public void setPlayerType(PlayerType type) {
        this.playerType = type;
        if(playerType == PlayerType.RANDOM) agent = new RandomAgent();
        if(playerType == PlayerType.MCTS) agent = new MCTSAgent(1000, (2*(1/Math.sqrt(2.00))));
        if(playerType == PlayerType.MCTSDQN) agent = new MCTSAgent(1000, 1.1, true);
        if(playerType == PlayerType.RULE) agent = new RuleAgent();
    }

    public int getMoney() { return money; }
    public void addMoney(int amount) { money += amount; }
    public boolean reduceMoney(int amount) {
        if(money - amount < 0) return false;
        money -= amount;
        return true;
    }

    public void setInvestor(boolean hasInvestor) { this.hasInvestor = hasInvestor; }
    public boolean hasInvestor(){return hasInvestor;}
    public int getIndex() { return index; }

    public ArrayList<Nation> getOwnedNations() { return ownedNations; }
    public void addNation(Nation nation) { ownedNations.add(nation); }
    public void removeNation(Nation nation) { ownedNations.remove(nation); }
    
    public ArrayList<Bond> getOwnedBonds() { return ownedBonds; }
    public void addBond(Bond bond) { ownedBonds.add(bond); }
    public void removeBond(Bond bond) { ownedBonds.remove(bond); }
    public void setOwnedBonds(ArrayList<Bond> ownedBonds) { this.ownedBonds = ownedBonds; }

    public void initialize(ArrayList<Bond> ownedBonds, ArrayList<Nation> ownedNations, long compLimitMCTS, double explorationValue) { 
        this.ownedBonds = ownedBonds;
        this.ownedNations = ownedNations; 
        if(playerType == PlayerType.RANDOM) agent = new RandomAgent();
        if(playerType == PlayerType.MCTS) agent = new MCTSAgent(compLimitMCTS, explorationValue);
        if(playerType == PlayerType.MCTSDQN) agent = new MCTSAgent(compLimitMCTS, explorationValue, true);
        if(playerType == PlayerType.RULE) agent = new RuleAgent();
    }
    
    public AgentInterface getAgent() { return agent; }
}
