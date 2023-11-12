package Group12.Imperial.gamelogic.agents;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.SerializationUtils;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.Player;
import Group12.Imperial.gamelogic.Nation.NationName;
import Group12.Imperial.gamelogic.Player.PlayerType;
import Group12.Imperial.gamelogic.actions.*;
import Group12.Imperial.gamelogic.gameboard.*;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.gameboard.map.*;
import Group12.Imperial.gui.board.rondel.RondelGUI.RondelChoice;

/*
 * TODO: 
 * Make random decision variations for all choices
 * Create structure for running simulations
 * Create a random simulation playout
 */

public class GameState implements Serializable{
    private int playerCount;
    private Player[] players;
    private Nation[] nations;
    private Map map;
    private TaxChart taxChart;
    private boolean gameFinished;
    private int currentPlayer;
    private int currentNation;
    private int tick;
    private PlayerType[] playerTypes;
    private RondelChoice[] rondel = { RondelChoice.MANEUVER, RondelChoice.TAXATION, RondelChoice.FACTORY, RondelChoice.PRODUCTION, RondelChoice.MANEUVER, RondelChoice.INVESTOR, RondelChoice.IMPORT, RondelChoice.PRODUCTION };
    private int[] rondelNationPosition;
    private Controller controller;
    public Bond[] AUbonds;
    public Bond[] ITbonds;
    public Bond[] FRbonds;
    public Bond[] GBbonds;
    public Bond[] GEbonds;
    public Bond[] RUbonds;
    

    public int getTick() {
        return tick;
    }



    /**
     * The gamestate object holds all of the snapshot information about a current game for use in conjunction with the AI bots.
     * @param players List of player objects in the game the game at the current snapshot..
     * @param nations List of nation objects in the game at the current snapshot.
     * @param map The map object of the game the game at the current snapshot.
     * @param taxChart Tax Chart of the game at the current snapshot.
     * @param gameFinished Whether the game is complete or not.
     * @param currentPlayer
     * @param currentNation
     * @param tick A integer keeping track of what tick the game is on, for analytical purposes.
     * @param controller
     */
    public GameState(Player[] players, Nation[] nations, int[] rondelNationPosition, Map map, TaxChart taxChart, boolean gameFinished, int playerCount, int currentPlayer, int currentNation, int tickCount, Controller controller, ArrayList<Bond[]> bonds){
        this.players = players;
        playerTypes = new PlayerType[playerCount];
        for(int i = 0; i < players.length; i++) this.playerTypes[i] = players[i].getType();
        this.nations = nations;
        this.rondelNationPosition = rondelNationPosition;
        this.map = map;
        this.taxChart = taxChart;
        this.gameFinished = gameFinished;
        this.playerCount = playerCount;
        this.currentPlayer = currentPlayer;
        this.currentNation = currentNation;
        this.tick = tickCount;
        this.controller = controller;
        this.AUbonds = bonds.get(0);
        this.ITbonds = bonds.get(1);
        this.FRbonds = bonds.get(2);
        this.GBbonds = bonds.get(3);
        this.GEbonds = bonds.get(4);
        this.RUbonds = bonds.get(5);
    }
    public GameState(){}

    public void setRondelPosition(int nationIndex, int choice){
        this.rondelNationPosition[nationIndex] = choice;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void incrementTurn(){
        currentNation++;
        if(currentNation>5) currentNation=0;
        currentPlayer = controller.getOwnerOfNation(currentNation);
    }

    public Player[] getPlayers() {
        return players;
    }

    public Nation[] getNations() {
        return nations;
    }

    public Map getMap() {
        return map;
    }

    public TaxChart getTaxChart() {
        return taxChart;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public RondelChoice[] getRondel() {
        return rondel;
    }

    public int getCurrentNation() {
        return currentNation;
    }

    public int[] getRondelNationPositions() { return rondelNationPosition; }
    
    
    //Not very smart, kinda brute force
    /**Returns a list of lists where each inner list contains: "{strength, unitType, fromLocation, toLocation1,toLocation2, toLocation3,....}" from and to locations 
     * @param controller Used for the CheckManeuver method (MAY NOT BE NECESSARY)
     * @return
     */
    public ArrayList<ArrayList<Integer>> getValidManeuvers(){
        ArrayList<ArrayList<Integer>> validManeuvers = new ArrayList<>();
        int mapSize = map.getAllMapLocations().length;

        for(int i = 0; i < mapSize ;i++){
            Unit currentUnit = map.getLocation(i).getUnit(currentNation, UnitType.SHIP);
            if(currentUnit != null){//if there are units from a nation at that map space
                ArrayList<Integer> n = new ArrayList<>();
                n.add(currentUnit.getStrength());
                n.add(1); // Adds 1 to indicate it is a ship
                n.add(i);
                ArrayList<Integer> toLocations = Maneuver.checkIfManeuverIsPossible(controller, map, currentNation, i, currentUnit.getType());
                n.addAll(toLocations);
                if(toLocations.size() != 0) validManeuvers.add(n);
            }
            currentUnit = map.getLocation(i).getUnit(currentNation, UnitType.ARMY);
            if(currentUnit != null){//if there are units from a nation at that map space
                ArrayList<Integer> n = new ArrayList<>();
                n.add(currentUnit.getStrength());
                n.add(0); // Adds 0 to indicate that it is an army
                n.add(i);
                ArrayList<Integer> toLocations = Maneuver.checkIfManeuverIsPossible(controller, map, currentNation, i, currentUnit.getType());
                n.addAll(toLocations);
                if(toLocations.size() != 0) validManeuvers.add(n);
            }
            
        }

       
        return validManeuvers;
    }

	public Controller getController() {
		return controller;
	}

    public boolean getGameFinished(){
        return gameFinished;
    }

    public PlayerType[] getPlayerTypes() { return this.playerTypes; }

    public static GameState getCloneGameState(GameState state) {
        Player[] playersCloned = getClonePlayers(state);
        Nation[] nationsCloned = getCloneNations(state);
        int[] rondelNationPositionCloned = getCloneRondelNationPositions(state);
        Map mapCloned = getCloneMap(state);
        TaxChart taxChartCloned = getCloneTaxChart(state);
        boolean gameFinishedCloned = state.getGameFinished();
        int playerCountCloned = state.getPlayerCount();
        int currentPlayerCloned = state.getCurrentPlayer();
        int currentNationCloned = state.getCurrentNation();
        int tickCountCloned = state.getTick();
        
        ArrayList<Bond[]> bondsCloned = getCloneBonds(state);
        GameState tmpState = new GameState(playersCloned, nationsCloned, rondelNationPositionCloned, mapCloned, taxChartCloned , gameFinishedCloned, playerCountCloned, currentPlayerCloned, currentNationCloned, tickCountCloned, null, bondsCloned);
        Controller controllerCloned = getCloneController(tmpState);
        return new GameState(playersCloned, nationsCloned, rondelNationPositionCloned, mapCloned, taxChartCloned , gameFinishedCloned, playerCountCloned, currentPlayerCloned, currentNationCloned, tickCountCloned, controllerCloned, bondsCloned);
    }

    public static Controller getCloneController(GameState state){
        return new Controller(state, false, 1);
    }
    
 
    public static Player[] getClonePlayers(GameState state){
        return SerializationUtils.clone(state.getPlayers());
    }

     public static Nation[] getCloneNations(GameState state){
        return SerializationUtils.clone(state.getNations());
    }

    public static int[] getCloneRondelNationPositions(GameState state) {
        return SerializationUtils.clone(state.getRondelNationPositions());
    }

    public static Map getCloneMap(GameState state){
        return SerializationUtils.clone(state.map);
    }

    public static TaxChart getCloneTaxChart(GameState state){
        return SerializationUtils.clone(state.getTaxChart());
    }

// public Bond[] AUbonds = { Bond.AU_2, Bond.AU_4, Bond.AU_6, Bond.AU_9, Bond.AU_12, Bond.AU_16, Bond.AU_20,
//             Bond.AU_25 };
//     public Bond[] ITbonds = { Bond.IT_2, Bond.IT_4, Bond.IT_6, Bond.IT_9, Bond.IT_12, Bond.IT_16, Bond.IT_20,
//             Bond.IT_25 };
//     public Bond[] FRbonds = { Bond.FR_2, Bond.FR_4, Bond.FR_6, Bond.FR_9, Bond.FR_12, Bond.FR_16, Bond.FR_20,
//             Bond.FR_25 };
//     public Bond[] GBbonds = { Bond.GB_2, Bond.GB_4, Bond.GB_6, Bond.GB_9, Bond.GB_12, Bond.GB_16, Bond.GB_20,
//             Bond.GB_25 };
//     public Bond[] GEbonds = { Bond.GE_2, Bond.GE_4, Bond.GE_6, Bond.GE_9, Bond.GE_12, Bond.GE_16, Bond.GE_20,
//             Bond.GE_25 };
//     public Bond[] RUbonds = { Bond.RU_2, Bond.RU_4, Bond.RU_6, Bond.RU_9, Bond.RU_12, Bond.RU_16, Bond.RU_20,
//             Bond.RU_25 };


    public static ArrayList<Bond[]> getCloneBonds(GameState state) {
        return SerializationUtils.clone(state.groupBonds());
    }

    public ArrayList<Bond[]> groupBonds(){
        ArrayList<Bond[]> ownedBonds = new ArrayList<>();
        ownedBonds.add(AUbonds);
        ownedBonds.add(ITbonds);
        ownedBonds.add(FRbonds);
        ownedBonds.add(GBbonds);
        ownedBonds.add(GEbonds);
        ownedBonds.add(RUbonds);
        return ownedBonds;
    }



    public Bond[] getBondArrayOnName(NationName name) {
        if(name == NationName.AUSTRIAHUNGARY) {
            return AUbonds;
        } else if(name == NationName.ITALY) {
            return ITbonds;
        } else if(name == NationName.FRANCE) {
            return FRbonds;
        } else if(name == NationName.GERMANY) {
            return GEbonds;
        } else if(name == NationName.GREATBRITAIN) {
            return GBbonds;
        } else if(name == NationName.RUSSIA) {
            return RUbonds;
        }
        return null;
    
    }

    @Override
    public String toString() {
        /*
        GameState 
       ++++++++++++++ Players ++++++++++++++
        ___________________________________
        ----- Player N
        -------- player N owned nations
        ------------Nation (1,2,3...)
        -------------- Treasury
        -------------- Unit locations
        -------- player n owned bonds
        -------- player n owned locations
        ___________________________________
        
        ++++++++++++++ State Info ++++++++++++++ 
        ----- currentplayer
        ----- currentNation
        ----- rondelpositions
        ----- tick
        ++++++++++++++++++++++++++++++++++++
        */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~GameState~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n++++++++++++++ Players ++++++++++++++ \n___________________________________\n");
        for(Player player : this.players){
            stringBuilder.append("----- Player " + player.getIndex() + "\n");
            stringBuilder.append("------------ Player treasury: " + player.getMoney()+ "\n");
                for(Nation nation : player.getOwnedNations()){
                    stringBuilder.append("------------ Nation: " + nation.getIndex() + "\n");
                    stringBuilder.append("-------------- Treasury: " + nation.getTreasury() + "\n");
                    stringBuilder.append("-------------- Unit Locations: " + nation.getUnitLocations().toString() + "\n");
                    stringBuilder.append("-------------- Map Unit Locations: ");
                    for(MapLocation loc : map.getAllMapLocations()) {
                        Unit[] unitsOfLoc = loc.getUnits();
                        for(Unit u : unitsOfLoc) {
                            if(u != null) stringBuilder.append("[N:" + u.getOwner().getIndex() + ", L:" + loc.getIndex() + "]");
                        }
                    }
                    stringBuilder.append("\n");
                    stringBuilder.append("-------------- Factory Locations: " );
                    for(Factory fac : nation.getFactories()) {
                        stringBuilder.append(fac.getLocationIndex() + ", ");
                    }
                    stringBuilder.append("\n");
                    stringBuilder.append("-------------- Owned Locations: ");
                    for(Integer loc : nation.getOwnedLocations()){
                        stringBuilder.append(loc + ", ");
                    }
                    stringBuilder.append("\n");
                    
                }

            stringBuilder.append("-------- Owned Bonds: ");
            for(Bond bond : player.getOwnedBonds()){
                stringBuilder.append(bond + ", ");
            }
            stringBuilder.append("\n");
            stringBuilder.append("___________________________________\n");
        }
        stringBuilder.append("++++++++++++++ State Info ++++++++++++++\n");
        
        stringBuilder.append("----- Current Player: " + currentPlayer + "\n");
        stringBuilder.append("----- Current Nation: " + currentNation + "\n");
        stringBuilder.append("----- Rondel Positions: " + Arrays.toString(rondelNationPosition) + "\n");
        stringBuilder.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");
        return stringBuilder.toString();
    }

}
