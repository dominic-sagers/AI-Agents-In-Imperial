package Group12.Imperial.gamelogic.agents;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable{

    public static int stateIdCount = 0;// Increments with each new node creadet and acts as an identifier.

    /** Id representing the GameState contained in this node. */
    private int stateId;
    private double wins = 0;
    /**
     * Used for MCTS algorithm to determine the value of this node as a choice or
     * outcome to be considered.
     */
    private double utility = -1;//Set to -1 for root node
    private double UCTutility = -1;
    /** Boolean representing whether or not this node is a root */
    private boolean isLeaf = true;
    /** The parent node of this Node, set to null if this Node is a root */
    private Node parent = null;
    /** This nodes gamestate. */
    private GameState state;
    /** The depth of this node, 0 if the node is a root. */
    private int depth = 0;
    /** The amount of times a node has been visited in the GameTree. (MCTS) */
    private int visitCount = 0;
    /** The list of nodes which share the same parent node of this node. */
    private ArrayList<Node> siblings = new ArrayList<>();
    /** List of this node's child nodes. */
    private ArrayList<Node> children = new ArrayList<>();

    private int rondelChoiceMade;
    private ArrayList<ArrayList<Integer>> maneuverMade;
    private int factoryChoiceMade;
    private ArrayList<ArrayList<Integer>> importChoiceMade;
    private int[] bondChoiceMade;
    private int initialNation;

    /**
     * This constructor makes a constructor with all defined parameters,
     * subsequent constructors create a Node with default parameters or a Node with
     * constructors to be defined later
     * 
     * @param parent   This particular Node's parent. If parent == null, then this
     *                 Node is a root.
     * @param state    The gamestate stored within this node.
     * @param utility  The utility value of this node (depending on the search
     *                 algorithm used).
     * @param depth    The current depth of this node in the tree.
     * @param siblings The nodes sharing the same parent node of this node.
     */
    public Node(Node parent, GameState state, double utility, int depth, ArrayList<Node> siblings) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
        this.state = state;
        this.utility = utility;
        this.siblings = siblings;
        this.stateId = Node.stateIdCount;
        this.initialNation = state.getCurrentNation();
        isLeaf();
        Node.stateIdCount++;
    }

    /**
     * Node constructor taking a parent Node object and a game state, this
     * constructor adds the new Node as a child to teh parent and uses
     * updateSiblings()
     * to update the children of the parent node to include the new node into the
     * sibling list.
     * 
     * @param parent
     * @param state
     */
    public Node(Node parent, GameState state) {
        this.parent = parent;
        this.stateId = Node.stateIdCount;
        if (parent != null) {
            parent.addChild(this);
        }
        this.state = state;
        if (parent != null) {
            this.depth = parent.getDepth() + 1;
        }
        //this.siblings = updateSiblings();
        this.stateId = Node.stateIdCount;
        this.initialNation = state.getCurrentNation();
        isLeaf();
        Node.stateIdCount++;
    }

    /**
     * A constructor only requiring a GameState object, is used when creating a root
     * node as the parent to this node will remain null.
     * 
     * @param state
     */
    public Node(GameState state) {
        this.state = state;
        this.stateId = Node.stateIdCount;
        this.initialNation = state.getCurrentNation();
        isLeaf();
        Node.stateIdCount++;
    }

    /**
     * Adds this node to the sibling lists of the nodes sharing the same parent.
     * 
     * @return This node's sibling list. (Used in constructor)
     */
    public ArrayList<Node> updateSiblings() {
        
        if (parent != null) {
            this.siblings = parent.getChildren();
            for (int i = 0; i < parent.getChildren().size(); i++) {
                if (parent.getChildren().get(i) != this) {
                    parent.getChildren().get(i).addSibling(this);
                }
            }
        }
        return this.siblings;
    }

    /**
     * Adds a child to this node and resets isLeaf to false if it was previously a
     * leaf.
     * 
     * @param child
     */
    public void addChild(Node child) {
        this.children.add(child);
        isLeaf();
    }

    public boolean isLeaf() {
        if (this.hasChild()) {
            isLeaf = false;
            return false;
        }
        return true;
    }

    /**
     * Loops through this node's children list and returns true if at least one
     * child exists.
     * 
     * @return True if a child exists, false otherwise.
     */
    public boolean hasChild() {
        if (this.children != null) {
            for (int i = 0; i < this.children.size(); i++) {
                if (this.children.get(i) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns boolean whether or not the node is a root node. Checks for a "null"
     * parent or depth == 0.
     * 
     * @return True if this node is a root, false otherwise.
     */
    public boolean isRoot() {
        if (depth == 0 || parent == null) {
            return true;
        } else {
            return false;
        }
    }

    public void makeChild(GameState state) {
        this.addChild(new Node(this, state));
    }

    public int getDepth() {
        return this.depth;
    }

    public int getInitialNation(){
        return this.initialNation;
    }

    public ArrayList<Node> getSiblings() {
        return siblings;
    }

    public void setSiblings(ArrayList<Node> siblings) {
        this.siblings = siblings;
    }

    public void addSibling(Node sibling) {
        siblings.add(sibling);
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public void incrementVisitCount() {
        this.visitCount += 1;
    }

    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public int getStateId() {
        return stateId;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public boolean backPropogate(Node n, Double result) {
        if (n.isRoot()) {
            if(n.getVisitCount()==0) n.incrementVisitCount();
            return true;
        } else {
            n.update(result);
            backPropogate(n.parent, result);
        }
        return false;
    }

    public void update(Double result) {
        this.incrementVisitCount();
        this.wins = result + wins;
        visitCount++;
        utility = wins / visitCount;

    }

    public Node getBestChild(){
        Node bestChild = null;
        double bestUtility = -2.0;
        ArrayList<Node> bestChildren = new ArrayList<>();
        for(Node child : children){
            if(bestChild == null || bestUtility < child.getUCTutility()){
                bestChildren = new ArrayList<>();
                bestChildren.add(child);
                bestChild = child;
                bestUtility = child.getUCTutility();
            }else if(bestUtility==child.getUCTutility()){
                bestChildren.add(child);
            }

        }
        if(bestChildren.size()>1){return bestChildren.get(RandomChoice.getRandomListChoice(bestChildren));}
        return bestChildren.get(0);
        
    }

    public void setRondelChoice(int choice) {
        this.rondelChoiceMade = choice;
    }

    public int getRondelChoice() {
        return this.rondelChoiceMade;
    }

    public void setMadeMoveManeuver(ArrayList<ArrayList<Integer>> maneuver) {
        maneuverMade = maneuver;
    }

    public void setMadeMoveFactoryChoice(int location) {
        factoryChoiceMade = location;
    }

    public void setMadeMoveImportChoice(ArrayList<ArrayList<Integer>> imports) {
        importChoiceMade = imports;
    }

    public void setMadeMoveBondBuyChoice(int[] bonds) {
        bondChoiceMade = bonds;
    }

    public ArrayList<ArrayList<Integer>> getManeuverMade() {
        return maneuverMade;
    }

    public int getFactoryChoiceMade() {
        return factoryChoiceMade;
    }

    public ArrayList<ArrayList<Integer>> getImportChoiceMade() {
        return importChoiceMade;
    }

    public int[] getBondChoiceMade() {
        return bondChoiceMade;
    }

    public void applyUCT(double explorationValue) {// UCT according to page 7 in A Survey of MCTS Methods paper
        this.UCTutility = this.utility + explorationValue * (Math.sqrt((2 * Math.log(this.visitCount)) / (this.parent.getVisitCount())));
    }

    public double getUCTutility() {
        return this.UCTutility;
    }

    public void print(int level){
        for(int i = 1; i < level; i++){
            System.out.print("\t");
        }
        System.out.println(this);
        for(Node child : this.getChildren()){
            child.print(level+1);
        }
    }
    public void printfw(int level, PrintWriter fw){
        for(int i = 1; i < level; i++){
            fw.print("\t");
        }
        fw.println(this);
        for(Node child : this.getChildren()){
            child.printfw(level+1,fw);
        }
    }

    public String toString(){
        String node = new String();
        String moveType = "";
        switch(rondelChoiceMade){
            case 0:
                moveType = "Maneuver";
            break;
            case 1:
                moveType = "Taxation";
            break;
            case 2:
                moveType = "Factory";
            break;
            case 3:
                moveType = "Production";
            break;
            case 4:
                moveType = "Maneuver";
            break;
            case 5:
                moveType = "Investor";
            break;
            case 6:
                moveType = "Import";
            break;
            case 7:
                moveType = "Production";
            break;
        }
        if(this.UCTutility != -1.0){
             node = "(State: " + this.stateId +"|Value: " + this.UCTutility+"|Move: "+ moveType + ")"; 
             return node;
        }
        node = "(State: " + this.stateId +"|Value: " + this.utility+")"; 
        return node;
    }
}
