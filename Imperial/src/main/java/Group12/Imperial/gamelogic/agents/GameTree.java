package Group12.Imperial.gamelogic.agents;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class GameTree implements Serializable {

    /**
     *The gametree is represented by a hashtable whering the index is key/value <stateId, Node> where each key is the unique state Id of each node in the 
     tree, this should be chronological as each stateId is created in the order each new node is made.
     */
    private Hashtable<Integer, Node> tree = new Hashtable<>(); 
    /** */
    private int maxDepth = 0;
    //2*(1/Math.sqrt(2.00))
    private double explorationValue = 2*(1/Math.sqrt(2.00));
    private Node bestNode;
   

    /** */
    private Node root;

    
    
    /**A GameTree can require a root Node or a state which is instantiated as a root Node to begin the tree.
     * @param root
     */
    public GameTree(Node root){
        tree.put(root.getStateId(), root);
        this.root = root;
        Node.stateIdCount = 0;
    }

    /**A GameTree can require a root Node or a state which is instantiated as a root Node to begin the tree.
     * @param state
     */
    public GameTree(GameState state){
        Node.stateIdCount = 0;
        Node root = new Node(null, state);
        tree.put(root.getStateId(), root);
        this.root = root;
        
    }

    public GameTree(GameState state, double explorationConstant){
        this.explorationValue = explorationConstant;
        Node.stateIdCount = 0;
        Node root = new Node(null, state);
        tree.put(root.getStateId(), root);
        this.root = root;
        
    }

    public Node getBestNode(){return bestNode;}

    public Node getRoot(){
        Node root = null;
        Set<Integer> keys = tree.keySet();

        for(Integer key : keys){
            if(tree.get(key).getParent()==null){
                root = tree.get(key);
                return root;
            }
        }
        System.out.println("couldnt find root :(");
        return root;
        
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    /**Adds a new node to the tree, if the depth of the newly added node is greater than the maxDepth of the tree, the maxValue is updated.
     * @param newNode
     */
    public void addNode(Node newNode){
        tree.put(newNode.getStateId(), newNode);
        if(newNode.getDepth()>maxDepth){maxDepth = newNode.getDepth();}        
    }

    public Hashtable<Integer, Node> getTree(){return tree;}

    /**Performs a Depth First search for a specified stateId in the GameTree, and returns the corresponding node. 
     * @param stateId the stateId of the node to search for.
     */
    public Node DFS(int stateId){
        Node foundNode = getNodeDFS(stateId, this.root, new ArrayList<Node>());

        if(foundNode.getStateId()!=stateId){
            System.out.println("Could not find node :(");
            return foundNode;
        }else{
            return foundNode;
        }

    }
    
    public boolean allNodesExplored(){

        Set<Integer> keys = tree.keySet();

        for(Integer key : keys){
            if(tree.get(key).getVisitCount() == 0){
                return false;
            }
        }

        return true;
        
    }

    public ArrayList<Node> getUnexploredNodes(){
        ArrayList<Node> unexploredNodes = new ArrayList<>();

        Set<Integer> keys = tree.keySet();

        for(Integer key : keys){
            if(tree.get(key).getVisitCount() == 0){
                unexploredNodes.add(tree.get(key));
            }
        }

        return unexploredNodes;
        

    }
    
    public Node getNextBestMove(){
        ArrayList<Node> maxNodes = new ArrayList<>();

        Node maxNode = null;
        int maxVisits = 0;
        for (Node child : root.getChildren()) {
            if(child.getVisitCount() > maxVisits){
                maxNodes = new ArrayList<>();
                maxVisits = child.getVisitCount();
                maxNodes.add(child);
            }else if(child.getVisitCount() == maxVisits){
                maxNodes.add(child);
            }
            // if (maxNode == null || maxNode.getUCTutility() < child.getUCTutility()) {
            //     maxNodes = new ArrayList<>();
            //     maxNodes.add(child);
            // }else if(maxNode.getUCTutility() == child.getUCTutility()){
            //     maxNodes.add(child);
            // }

        }
        if(maxNodes.size()>1){maxNode = maxNodes.get(RandomChoice.getRandomListChoice(maxNodes));}else{
            maxNode = maxNodes.get(0);
        }
        if(maxNodes.size() == 0) {
            System.out.println("No nodes in the tree");
        }
        return maxNode;
    }

    public Node getNextBestNode(){//TODO: Make this more intelligent (Sorting max duplicate utilites based on some heuristice (like not choosing maneuver))

        Node currentNode = root;

        while(!currentNode.isLeaf()){
            Node nextNode = currentNode.getBestChild();
            currentNode = nextNode;
        }
        return currentNode;

    } 


    public void applyUCT(int explorationValue){
        Set<Integer> keys = tree.keySet();
        for(Integer key : keys){
            tree.get(key).applyUCT(explorationValue);
        }
    }
    
    public void applyUCT(){
        Set<Integer> keys = tree.keySet();
        for(Integer key : keys){
            if(!tree.get(key).isRoot()) tree.get(key).applyUCT(this.explorationValue);
        }
    }


    public Node getNodeDFS(int index, Node nextNode, ArrayList<Node> visitedNodes){

        ArrayList<Node> currentVisitedNodes = visitedNodes;


        if(nextNode.getStateId() == index){
            return nextNode;
        }else{ 
            if(nextNode.hasChild()){
                
                for(Node child : nextNode.getChildren()){
                    if(!currentVisitedNodes.contains(child)){
                        currentVisitedNodes.add(child);
                        nextNode = getNodeDFS(index, child, currentVisitedNodes);
                        if (nextNode.getStateId()==index){return nextNode;}
                    }
                }
                
                return nextNode.getParent();
                
            }else{
                return nextNode.getParent();
            }
        }    
    }

    public void printTree(){
        root.print(1);
    }
    public void printTreeWriter(PrintWriter fw){
        root.printfw(1, fw);
    }
    
   

public static void main(String[] args){
    
    GameState testState = new GameState();   
    
    Node node1 = new Node(null, testState);//id 0
        Node node2 = new Node(node1, testState);//id 1
            Node node4 = new Node(node2, testState);//id 2
            Node node5 = new Node(node2, testState);//id 3
            Node node6 = new Node(node2, testState);//id 4
                Node node7 = new Node(node6,testState); //id 5
                Node node8 = new Node(node6, testState);//id 6

        Node node3 = new Node(node1, testState);//id 7
            Node node9 = new Node(node3, testState); //id 8
                Node node10 = new Node(node9, testState);//id 9
                Node node11 = new Node (node9, testState);//id 10
            Node node12 = new Node(node3, testState);//id 11

    GameTree tree = new GameTree(node1);
    tree.addNode(node2);
    tree.addNode(node3);
    tree.addNode(node4);
    tree.addNode(node5);
    tree.addNode(node6);
    tree.addNode(node7);
    tree.addNode(node8);
    tree.addNode(node9);
    tree.addNode(node10);
    tree.addNode(node11);
    tree.addNode(node12);
    Set<Integer> keys = tree.getTree().keySet();

    tree.printTree();

}


}
