package Group12.Imperial.gamelogic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import Group12.Imperial.gamelogic.Nation.NationName;
import Group12.Imperial.gamelogic.Player.PlayerType;
import Group12.Imperial.gamelogic.actions.Import;
import Group12.Imperial.gamelogic.actions.Investor;
import Group12.Imperial.gamelogic.actions.Maneuver;
import Group12.Imperial.gamelogic.actions.Production;
import Group12.Imperial.gamelogic.actions.Taxation;
import Group12.Imperial.gamelogic.agents.*;
import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.gameboard.Factory;
import Group12.Imperial.gamelogic.gameboard.TaxChart;
import Group12.Imperial.gamelogic.gameboard.Unit;
import Group12.Imperial.gamelogic.gameboard.Bond.BondType;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.gameboard.map.Map;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation;
import Group12.Imperial.gui.ControllerGUI;
import Group12.Imperial.gui.EventKey;
import Group12.Imperial.gui.board.rondel.RondelGUI.RondelChoice;
import Group12.Imperial.launchers.DQNTrainer;
import javafx.application.Platform;

public class Controller implements Serializable {

    public final boolean DEBUG;

    private ControllerGUI controllerGUI;

    private int playerCount;
    private PlayerType[] playerTypes;
    private Player[] players;
    private Nation[] nations;
    private Map map;
    private TaxChart taxChart;
    public int tickCount = 0;

    public boolean gameFinished;
    private boolean investorWasPassed = false;

    private RondelChoice[] rondel = { RondelChoice.MANEUVER, RondelChoice.TAXATION, RondelChoice.FACTORY,
            RondelChoice.PRODUCTION, RondelChoice.MANEUVER, RondelChoice.INVESTOR, RondelChoice.IMPORT,
            RondelChoice.PRODUCTION };
  

    private int[] rondelNationPosition = { -1, -1, -1, -1, -1, -1 };

    private int[] playerScores;

    public Bond[] AUbonds = { new Bond(BondType.AU_2), new Bond(BondType.AU_4), new Bond(BondType.AU_6), new Bond(BondType.AU_9), new Bond(BondType.AU_12), new Bond(BondType.AU_16), new Bond(BondType.AU_20),
            new Bond(BondType.AU_25) };
    public Bond[] ITbonds = { new Bond(BondType.IT_2), new Bond(BondType.IT_4), new Bond(BondType.IT_6), new Bond(BondType.IT_9), new Bond(BondType.IT_12), new Bond(BondType.IT_16), new Bond(BondType.IT_20),
            new Bond(BondType.IT_25) };
    public Bond[] FRbonds = { new Bond(BondType.FR_2), new Bond(BondType.FR_4), new Bond(BondType.FR_6), new Bond(BondType.FR_9), new Bond(BondType.FR_12), new Bond(BondType.FR_16), new Bond(BondType.FR_20),
            new Bond(BondType.FR_25) };
    public Bond[] GBbonds = { new Bond(BondType.GB_2), new Bond(BondType.GB_4), new Bond(BondType.GB_6), new Bond(BondType.GB_9), new Bond(BondType.GB_12), new Bond(BondType.GB_16), new Bond(BondType.GB_20),
            new Bond(BondType.GB_25) };
    public Bond[] GEbonds = { new Bond(BondType.GE_2), new Bond(BondType.GE_4), new Bond(BondType.GE_6), new Bond(BondType.GE_9), new Bond(BondType.GE_12), new Bond(BondType.GE_16), new Bond(BondType.GE_20),
            new Bond(BondType.GE_25) };
    public Bond[] RUbonds = { new Bond(BondType.RU_2), new Bond(BondType.RU_4), new Bond(BondType.RU_6), new Bond(BondType.RU_9), new Bond(BondType.RU_12), new Bond(BondType.RU_16), new Bond(BondType.RU_20),
            new Bond(BondType.RU_25) };

    private int currentPlayer;
    private int currentNation;
    private GameState currentState;

    private ArrayList<ArrayList<Integer>> movableUnits;

    private int winnerPlayer = -1;

    private final EventKey EVENTKEY = new EventKey();
    private boolean isSimulation;
    private boolean isTraining;
    private boolean initialisingGame;
    private boolean getInvestorState;
    private boolean shouldStop = false;

    private int trainingPlayer = 1;
    private DQNTrainer trainer;
    private InvestorState nextInvestorState;

    private long startTime;
    public long timeTook;

    public long compLimitMCTS = 1000;
    public double explorationValue = 1.1;

    public Controller(ControllerGUI controllerGUI, int playerCount, PlayerType[] playerTypes, boolean DEBUG) {
        this.controllerGUI = controllerGUI;
        this.playerCount = playerCount;
        this.playerTypes = playerTypes;
        this.DEBUG = false;
        init();
        startTime = System.nanoTime();
    }

    public Controller(GameState initialState, boolean DEBUG, int l) {
        this.DEBUG = DEBUG;
        init(initialState, l);
    }


    public Controller(int playerCount, PlayerType[] playerTypes, boolean DEBUG, DQNTrainer dqnTrainer) {
        this.playerCount = playerCount;
        this.playerTypes = playerTypes;
        this.DEBUG = false;
        this.trainer = dqnTrainer;
        init();
        startTime = System.nanoTime();
	}

	private void init() {
        this.players = new Player[playerCount];
        this.nations = new Nation[6];
        this.gameFinished = false;
        this.taxChart = new TaxChart();
        initNations();
        initPlayers();
        this.map = new Map(nations);
        setUpGame();
        tickCount = 0;
    }

    private void init(GameState initialState, int l) {
        this.gameFinished = false;
        this.playerCount = initialState.getPlayerCount();
        this.players = initialState.getPlayers();
        this.nations = initialState.getNations();
        this.playerTypes = initialState.getPlayerTypes();
        for(int i = 0; i < playerTypes.length; i++) playerTypes[i] = PlayerType.RANDOM;
        for(int i = 0; i < players.length; i++) players[i].setPlayerType(playerTypes[i]);
        this.map = initialState.getMap();
        this.taxChart = initialState.getTaxChart();
        this.currentNation = initialState.getCurrentNation();
        this.currentPlayer = initialState.getCurrentPlayer();
        this.tickCount = initialState.getTick();
        this.rondelNationPosition = initialState.getRondelNationPositions();
        this.AUbonds = initialState.AUbonds;
        this.FRbonds = initialState.FRbonds;
        this.ITbonds = initialState.ITbonds;
        this.GBbonds = initialState.GBbonds;
        this.GEbonds = initialState.GEbonds;
        this.RUbonds = initialState.RUbonds;
        updateFactoriesInMap();
        updateBondOwners();
        if(l == 2) {
            getInvestorState = true;
        }
    }

    public int[] start() throws InterruptedException {
        isSimulation = false;
        engine();
        return playerScores;
    }
 
    public InvestorState getNextInvestorState() throws InterruptedException {
        isSimulation = false;
        engine();
        
        return nextInvestorState;
    }

    public void startSimulationGUI() throws InterruptedException {
        isSimulation = true;
        engineSimulationGUI();
    }

    public void startTrainingEpisode() throws InterruptedException{
        isSimulation = false;
        isTraining = true;
        engine();
    }

    private void engineSimulationGUI() throws InterruptedException {
        while (!gameFinished) {
            currentState = new GameState(players, nations, rondelNationPosition, map, taxChart, gameFinished, playerCount, currentPlayer, currentNation, tickCount, this, groupBonds());
            tickCount++;
            tick();
            recordMapPositions();
            currentState = new GameState(players, nations, rondelNationPosition, map, taxChart, gameFinished, playerCount, currentPlayer, currentNation,
                    tickCount, this, groupBonds());

            Platform.enterNestedEventLoop(EVENTKEY);
        }
    }

    private int engine() throws InterruptedException {
        
        currentState = new GameState(players, nations, rondelNationPosition, map, taxChart, gameFinished, playerCount, currentPlayer, currentNation,
                tickCount, this, groupBonds());

        while (!gameFinished) {
            currentState = new GameState(players, nations, rondelNationPosition, map, taxChart, gameFinished, playerCount, currentPlayer, currentNation,
                tickCount, this, groupBonds());
            if(shouldStop) return -1;
            if(DEBUG) {
                //System.out.println("++++++++++ TICK Player " + currentPlayer + ", Nation " + nations[currentNation].getName().stringRepresentation + "++++++++++");
                //System.out.println(" -- Tick Count " + tickCount);
                //System.out.println(" -- Rondel Positions: [" + rondel[rondelNationPosition[0]] + ", " +rondel[rondelNationPosition[1]] + ", " + rondel[rondelNationPosition[2]] + ", " + rondel[rondelNationPosition[3]] + ", " + rondel[rondelNationPosition[4]] + ", " + rondel[rondelNationPosition[5]] +"]");
            }
            tickCount++;
            tick();
            if(controllerGUI != null) recordMapPositions();
            currentState = new GameState(players, nations, rondelNationPosition, map, taxChart, gameFinished, playerCount, currentPlayer, currentNation,
                    tickCount, this, groupBonds());
    
        }
        return winnerPlayer;
    }

    public void tick() throws InterruptedException {
        investorWasPassed = false;
        for(int i = 0; i < players.length; i++) {
            if(players[i].hasInvestor()) {
                //System.out.println("Player " + i + " has the investor card");
            }
        }
        int previousPosition = rondelNationPosition[currentNation];
        if(players[currentPlayer].getType() == PlayerType.HUMAN) {
            rondelNationPosition[currentNation] = controllerGUI.makeRondelChoice(currentPlayer, currentNation);
        } else {
            rondelNationPosition[currentNation] = players[currentPlayer].getAgent().makeRondelChoice(currentState);
        }
        if(controllerGUI != null && players[currentPlayer].getType() != PlayerType.HUMAN) controllerGUI.updateRondelManually(currentPlayer, currentNation, rondelNationPosition[currentNation]);
        RondelChoice choice = rondel[rondelNationPosition[currentNation]];
        if(isSimulation) controllerGUI.updateMenuBar(currentPlayer, currentNation, choice.toString(), false);

        if(players[currentPlayer].getType() != PlayerType.HUMAN) {
            int currentPosition = rondelNationPosition[currentNation];
            if(previousPosition < 5 && (currentPosition > 5 || currentPosition <= 2) && previousPosition != -1) investorWasPassed = true;
        }

        switch (choice) {
            case FACTORY:
                int locationChoice = -1;
                if(players[currentPlayer].getType() == PlayerType.HUMAN) {
                    locationChoice = controllerGUI.makeFactoryChoice(currentPlayer, currentNation);
                } else {
                    locationChoice = players[currentPlayer].getAgent().makeFactoryChoice(currentState);
                }
                if(locationChoice != -1){
                    buildFactory(locationChoice, currentNation);
                    nations[currentNation].reduceMoney(5);
                }
                
                if(controllerGUI != null) updateGUI();
                break;
            case IMPORT:
                if(players[currentPlayer].getType() == PlayerType.HUMAN) {
                    Import.makeImportChoice(this, nations[currentNation], DEBUG);
                } else {
                    ArrayList<ArrayList<Integer>> choices =  players[currentPlayer].getAgent().makeImportChoice(currentState);
                    if(controllerGUI != null) {
                        for(ArrayList<Integer> importChoice : choices) {
                            int choiceNew = -1;
                            if(importChoice.get(1) == 0) {
                                choiceNew = 1;
                            } else if(importChoice.get(1) == 1){
                                choiceNew = 0;
                            }
                            controllerGUI.addUnit(currentPlayer, currentNation, importChoice.get(0), choiceNew, false);
                        }
                    }
                        
                }

                break;
            case INVESTOR:
                Investor.makeInvestorChoice(this, players, nations);
                break;
            case MANEUVER:
                movableUnits = new ArrayList<>();
                ArrayList<Integer> unitLocations = nations[currentNation].getUnitLocations();
                for (Integer unitLocation : unitLocations) {
                    ArrayList<Integer> unitInfo = new ArrayList<>();
                    unitInfo.add(unitLocation);
                    int strength = 0;
                    Unit unitShip = map.getLocation(unitLocation.intValue()).getUnit(currentNation, UnitType.SHIP);
                    if (unitShip != null)
                        strength += unitShip.getStrength();
                    Unit unitArmy = map.getLocation(unitLocation.intValue()).getUnit(currentNation, UnitType.ARMY);
                    if (unitArmy != null)
                        strength += unitArmy.getStrength();
                    unitInfo.add(strength);
                    movableUnits.add(unitInfo);
                }

                if(players[currentPlayer].getType() == PlayerType.HUMAN) {
                    controllerGUI.makeManeuverChoice(currentPlayer, currentNation);
                } else {
                    ArrayList<ArrayList<Integer>> moves = players[currentPlayer].getAgent().makeManeuverChoice(currentState);
                    
                }
                break;
            case PRODUCTION:
                ArrayList<ArrayList<Integer>> list = Production.produce(map, nations[currentNation]);
                for (int i = 0; i < list.size(); i++) {
                    if(controllerGUI != null) controllerGUI.addUnit(currentPlayer, currentNation, list.get(i).get(0), list.get(i).get(1), true);
                }
                break;
            case TAXATION:
                Taxation.taxation(this, map, nations[currentNation], taxChart);
                if(controllerGUI != null) updateGUI();
                break;
        }
        if(controllerGUI != null) updateGUI();

        if (investorWasPassed) {
            //System.out.println("Investor was passed");
            Investor.investorWasPassed(this, players);
        }

        currentNation++;
        if (currentNation > 5)
            currentNation = 0;

        currentPlayer = getOwnerOfNation(currentNation);

    }

    public int getOwnerOfNation(int nation) {
        for (Player player : players) {
            ArrayList<Nation> ownedNations = player.getOwnedNations();
            for (Nation n : ownedNations) {
                if (n.getIndex() == nation)
                    return player.getIndex();
            }
        }
        return -1;
    }

    

    public boolean isFactoryBuildLegal(int location) {
        if (nations[currentNation].getTreasury() - 5 < 0)
            return false;
        if (map.getLocation(location).isHomeProvince()
                && (map.getLocation(location).getOwner().getIndex() == currentNation)
                && (map.getLocation(location).getFactory() == null)) {
            ArrayList<Integer> nationsPresent = map.getLocation(location).getNationsPresent();
            for (Integer nationIndex : nationsPresent) {
                if (nationIndex != currentNation) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isManeuverLegal(int fromLocation, int toLocation, UnitType unitType, int amount) {
        boolean isManeuverLegal = Maneuver.checkManeuver(this, map, currentNation, fromLocation, toLocation, unitType,
                amount, movableUnits);
        if (isManeuverLegal) {
            for (int i = 0; i < movableUnits.size(); i++) {
                if (movableUnits.get(i).get(0) == fromLocation) {
                    int strength = movableUnits.get(i).get(1).intValue();
                    movableUnits.get(i).set(1, strength - 1);
                }
            }
        }
        return isManeuverLegal;
    }

    public void buildFactory(int locationChoice, int nation) {
        Factory factory = new Factory(map.getLocation(locationChoice),
                map.getLocation(locationChoice).getFactoryType());
        map.getLocation(locationChoice).buildFactory(factory);
        nations[nation].addFactory(factory);
        if(controllerGUI != null && !initialisingGame && players[currentPlayer].getType() != PlayerType.HUMAN) controllerGUI.buildInitialFactory(locationChoice);
    }

    public Nation getNationOnIndex(int index) {
        return nations[index];
    }

    private void updateGUI() {
        for(int i = 0; i < nations.length; i++) {
            controllerGUI.updateNationScore(i, nations[i].getPowerPoints());
            controllerGUI.updateNationTreasury(i, nations[i].getTreasury());
            controllerGUI.updateTaxChart(currentNation, taxChart.getNationPosition(currentNation));
        }
        for(int i = 0; i < players.length; i++) {
            ArrayList<Bond> playersBonds = players[i].getOwnedBonds();
            String[] bondsGUI = { "", "", "", "", "", "" };
            for (Bond bond : playersBonds) {
                bondsGUI[bond.getNationName().index] = bondsGUI[bond.getNationName().index] + bond.getValue() + ", ";
                // bondsGUI[new Bond(BondType.getNationName().index] = bondsGUI[new Bond(BondType.getNationName().index] + new Bond(BondType.getValue() + ", ";
            }
            controllerGUI.updatePlayerTreasury(i, players[i].getMoney(), bondsGUI);
        }
        
        
    }

    private void setUpGame() {
        for (Nation nation : nations) {
            nation.addMoney(11);
        }
        initialisingGame = true;
        buildFactory(42, 0);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(42);
        buildFactory(43, 0);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(43);

        buildFactory(38, 1);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(38);
        buildFactory(39, 1);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(39);

        buildFactory(19, 2);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(19);
        buildFactory(22, 2);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(22);

        buildFactory(11, 3);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(11);
        buildFactory(13, 3);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(13);

        buildFactory(30, 4);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(30);
        buildFactory(31, 4);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(31);

        buildFactory(46, 5);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(46);
        buildFactory(49, 5);
        if(controllerGUI != null) controllerGUI.buildInitialFactory(49);

        for (int i = 0; i < 6; i++) {
            currentNation = i;
            currentPlayer = getOwnerOfNation(currentNation);
            if(controllerGUI != null) updateGUI();
        }
        currentNation = 0;
        currentPlayer = getOwnerOfNation(currentNation);
        initialisingGame = false;
    }

    private void initPlayers() {
        String[] filePaths = { "", "player_init_2.csv", "player_init_3.csv", "", "", "player_init_6.csv" };
        Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(filePaths[playerCount - 1]));
        int playerCounter = 0;
        while (scanner.hasNextLine()) {
            Scanner row = new Scanner(scanner.nextLine());
            row.useDelimiter(",");

            int money = 2;
            players[playerCounter] = new Player(playerCounter, money, playerTypes[playerCounter]);
            ArrayList<Nation> ownedNations = new ArrayList<>();
            ArrayList<Bond> ownedBonds = new ArrayList<>();

            int rowCounter = 0;
            while (row.hasNext()) {
                String value = row.next();
                if (value.equals("end")) {
                    rowCounter++;
                } else {
                    if (rowCounter == 0) {
                        ownedNations.add(nations[Integer.parseInt(value)]);
                    } else if (rowCounter == 1) {
                        ownedBonds.add(AUbonds[Integer.parseInt(value)]);
                        AUbonds[Integer.parseInt(value)].setPlayer(players[playerCounter]);
                    } else if (rowCounter == 2) {
                        ownedBonds.add(ITbonds[Integer.parseInt(value)]);
                        ITbonds[Integer.parseInt(value)].setPlayer(players[playerCounter]);
                    } else if (rowCounter == 3) {
                        ownedBonds.add(FRbonds[Integer.parseInt(value)]);
                        FRbonds[Integer.parseInt(value)].setPlayer(players[playerCounter]);
                    } else if (rowCounter == 4) {
                        ownedBonds.add(GBbonds[Integer.parseInt(value)]);
                        GBbonds[Integer.parseInt(value)].setPlayer(players[playerCounter]);
                    } else if (rowCounter == 5) {
                        ownedBonds.add(GEbonds[Integer.parseInt(value)]);
                        GEbonds[Integer.parseInt(value)].setPlayer(players[playerCounter]);
                    } else if (rowCounter == 6) {
                        ownedBonds.add(RUbonds[Integer.parseInt(value)]);
                        RUbonds[Integer.parseInt(value)].setPlayer(players[playerCounter]);
                    }
                }
            }
            players[playerCounter].initialize(ownedBonds, ownedNations, compLimitMCTS , explorationValue);
            playerCounter++;
        }
        
        players[getOwnerOfNation(5)].setInvestor(true);
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
    

    private void initNations() {
        int[] homeProvinces0 = { 40, 41, 42, 43, 44 };
        nations[0] = new Nation(0, NationName.AUSTRIAHUNGARY, homeProvinces0);
        int[] homeProvinces1 = { 35, 36, 37, 38, 39 };
        nations[1] = new Nation(1, NationName.ITALY, homeProvinces1);
        int[] homeProvinces2 = { 19, 20, 21, 22, 23 };
        nations[2] = new Nation(2, NationName.FRANCE, homeProvinces2);
        int[] homeProvinces3 = { 9, 10, 11, 12, 13 };
        nations[3] = new Nation(3, NationName.GREATBRITAIN, homeProvinces3);
        int[] homeProvinces4 = { 30, 31, 32, 33, 34 };
        nations[4] = new Nation(4, NationName.GERMANY, homeProvinces4);
        int[] homeProvinces5 = { 45, 46, 47, 48, 49 };
        nations[5] = new Nation(5, NationName.RUSSIA, homeProvinces5);
    }


    public void gameFinished() {
        gameFinished = true;
        calculateFinalScore();
        timeTook = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }

    private void calculateFinalScore() {
        int[] nationFactors = new int[6];
        int[] powerPoints = new int[6];
        for(int i = 0; i < nationFactors.length; i++) {
            powerPoints[i] = nations[i].getPowerPoints();
            if(powerPoints[i] < 5) {
                nationFactors[i] = 0;
            } else if(powerPoints[i] < 10) {
                nationFactors[i] = 1;
            } else if(powerPoints[i] < 15) {
                nationFactors[i] = 2;
            } else if(powerPoints[i] < 20) {
                nationFactors[i] = 3;
            } else if(powerPoints[i] < 25) {
                nationFactors[i] = 4;
            } else if(powerPoints[i] == 25) {
                nationFactors[i] = 5;
            }
        }
        playerScores = new int[players.length];
        int winner = 0;
        for(int i = 0; i < players.length; i++) {
            int score = 0;
            ArrayList<Bond> ownedBonds = players[i].getOwnedBonds();
            for(Bond bond : ownedBonds) {
                score += bond.getInterest() * nationFactors[bond.getNationName().index];
            }
            score += players[i].getMoney();
            
            playerScores[i] = score;
            if(score >= playerScores[winner]) {
                winner = i;
            }
        }
        winnerPlayer = winner;

    }

    public void payPlayer(int bonus) {
        players[currentPlayer].addMoney(bonus);
    }

    public void battleAtLocation(int location) {
        if(map.getLocation(location).getNationsPresent().size() == 1) {
            System.out.println("Only 1 nation present");
        }
        ArrayList<Integer> nationsPresent = map.getLocation(location).getNationsPresent();
        int indexToMove = nationsPresent.indexOf(Integer.valueOf(currentNation));

        nationsPresent.remove(indexToMove);
        nationsPresent.add(0, Integer.valueOf(currentNation));
        boolean battleFinished = false;
        // TODO: Add option without GUI
        for (Integer n : nationsPresent) {
            if (battleFinished) break;

            int battleChoice;
            //TODO: Make this right as well
            if(players[getOwnerOfNation(n)].getType() == PlayerType.HUMAN) {
                battleChoice = controllerGUI.makeBattleChoice(getOwnerOfNation(n), n, location, nationsPresent);
            }else{
                ArrayList<Integer> battleOptions = new ArrayList<>();
               
                if(currentNation != n) {
                    battleOptions.add(currentNation);
                } else {
                    battleOptions = nationsPresent;
                }
                battleChoice = players[getOwnerOfNation(n)].getAgent().makeBattleChoice(currentState, n, location, battleOptions);
            }
            
            if (battleChoice == 0) {
                battleFinished = false;
                Unit[] unitsOfNation = map.getLocation(location).getAllUnitsOfNation(n.intValue());
                for (Unit u : unitsOfNation)
                    if (u != null)
                        u.setIsHostile(false);

            } else {
                battleFinished = true;
                ArrayList<Integer> nationsForBattle = new ArrayList<>();
                nationsForBattle.add(n);
                nationsForBattle.add(Integer.valueOf(battleChoice - 1));
                ArrayList<Integer> nationsPresentBefore = map.getLocation(location).battle(this, nationsForBattle);

                ArrayList<Integer> nationsPresentAfter = map.getLocation(location).getNationsPresent();
                for (Integer nationBefore : nationsPresentBefore) {
                    if (!nationsPresentAfter.contains(nationBefore))
                        nations[nationBefore.intValue()].removeUnitLocation(location);
                }

                if (nationsPresentAfter.size() == 0) {
                    
                } else if (nationsPresentAfter.size() == 1) {
                    int survivorNation = nationsPresentAfter.get(0).intValue();
                    
                    Unit[] unitsOfNation = map.getLocation(location).getAllUnitsOfNation(survivorNation);
                    
                    for (Unit u : unitsOfNation) if (u != null) if(controllerGUI != null) controllerGUI.updateUnit(survivorNation, location, u.getStrength(), u.getType());
                    
                    Nation ownerOfLocation = map.getLocation(location).getOwner();
                    Nation newOwner = nations[survivorNation];
                    
                    if(!map.getLocation(location).isHomeProvince()) { 
                        map.getLocation(location).setOwner(newOwner);
                        newOwner.addOwnedLocation(location);
                        if(ownerOfLocation != null && ownerOfLocation.getIndex() != newOwner.getIndex()) {
                            ownerOfLocation.removeOwnedLocation(location);
                        } 
                        if(controllerGUI != null) {  
                            controllerGUI.addFlag(newOwner.getIndex(), location);
                        } 
                    }
                    
                } else {
                    for (Integer i : nationsPresentAfter) {
                        for (Integer j : nationsPresentBefore) {
                            if (j != i) {
                                //if(controllerGUI != null) controllerGUI.removeUnit(j.intValue(), location);
                            } else {
                                Unit[] unitsOfNation = map.getLocation(location).getAllUnitsOfNation(i.intValue());
                                for (Unit u : unitsOfNation)
                                    if (u != null)
                                        if(controllerGUI != null) controllerGUI.updateUnit(i.intValue(), location, u.getStrength(), u.getType());
                            }
                        }
                    }
                    
                    Nation ownerOfLocation = map.getLocation(location).getOwner();
                    if(n == ownerOfLocation.getIndex() || battleChoice - 1 == ownerOfLocation.getIndex()) {
                        Nation newOwner = null;
                        if(nationsPresentAfter.contains(n)){
                            newOwner = nations[n];
                            
                        }else if(nationsPresentAfter.contains(battleChoice - 1)){
                            newOwner = nations[battleChoice-1];
                        }
                        if(newOwner != null) map.getLocation(location).setOwner(newOwner);
                        
                        if(newOwner != null) { 
                            newOwner.addOwnedLocation(location);
                        } else if(ownerOfLocation != null) {
                            ownerOfLocation.removeOwnedLocation(location);
                        }
                        if(ownerOfLocation != null && newOwner != null && ownerOfLocation.getIndex() != newOwner.getIndex() ) {
                            ownerOfLocation.removeOwnedLocation(location);
                        } 
                        if(controllerGUI != null) {
                            if(newOwner == null) {
                                controllerGUI.removeFlag(location);
                            } else {
                                controllerGUI.addFlag(newOwner.getIndex(), location);
                            }
                        }
                    }      
                }
            }
            if (battleFinished) break;
        }
    }

    public void addGUIFlag(int nation, int elementindex) {
        if(controllerGUI != null) controllerGUI.addFlag(nation, elementindex);
    }

    public boolean makeIsHostileChoice() {
        if(players[currentPlayer].getType() == PlayerType.HUMAN) {
            return controllerGUI.makeIsHostileChoice(currentPlayer);
        } else {
            // TODO: Make this right
            return players[currentPlayer].getAgent().makeHostileChoice(currentState, currentPlayer, currentNation, null);
        }
    }

    public void moveUnitGUI(int nation, UnitType type, int fromLocation, int toLocation) {
        if(controllerGUI != null) controllerGUI.moveUnitAfterManeuver(nation, type, fromLocation, toLocation);
    }

    public GameState getGameState() {
        return new GameState(players, nations, rondelNationPosition, map, taxChart, gameFinished, playerCount, currentPlayer, currentNation, tickCount, this, groupBonds());
    }

    public void finishedImport() {
        Import.isFinished();
    }

    public boolean isImportLegal(int locationIndex, UnitType unitType) {

        return Import.isImportLegal(this, currentNation, map, locationIndex, unitType);
    }

    public void makeImportChoice() {
        if(controllerGUI != null) updateGUI();
        if(players[currentPlayer].getType() == PlayerType.HUMAN) {
            controllerGUI.makeImportChoice(currentPlayer, currentNation);
        } else {
            System.out.println("IMPORT WRONG PLAYER");
        }
            
        
    }

    public boolean isRondelMoveLegal(int newRondelIndex) {
        investorWasPassed = false;
        int currentPosition = rondelNationPosition[currentNation];
        if (currentPosition == -1)
            return true;

        for (int i = 1; i <= 6; i++) {
            currentPosition++;
            if (currentPosition > 7)
                currentPosition = 0;
            if (currentPosition == newRondelIndex) {
                if (i < 4) {  
                    return true;

                } else {
                    int extraStepsTaken = i - 3;
                    if (players[currentPlayer].getMoney() >= extraStepsTaken * 2) {
                        players[currentPlayer].reduceMoney(extraStepsTaken * 2);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else if (currentPosition == 5) {
                investorWasPassed = true;
            }
        }
        return false;
    }

    public int[] makeBondBuyChoice(Player player, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCost) throws InterruptedException {

        if(getInvestorState && player.getIndex() == trainingPlayer) {
            
            nextInvestorState = new InvestorState(currentState);
            shouldStop = true;
            int[] tmp = new int[2];
            tmp[0] = -1;
            return tmp;
        }
        //System.out.println("Player " + player.getIndex() + " is making a bond buy choice");
        if(!isTraining) {
            if(player.getType() == PlayerType.HUMAN) {
                return controllerGUI.makeBondBuyChoice(currentPlayer, currentNation, possibleActions, associatedCost);
            } else {
                
                return player.getAgent().makeBondBuyChoice(currentState, possibleActions, associatedCost);
            }
        } else {
            if(player.getIndex() == trainingPlayer) {
                return trainer.makeInvestorChoice(possibleActions, associatedCost, new InvestorState(currentState));
            } else {
                return player.getAgent().makeBondBuyChoice(currentState, possibleActions, associatedCost);
            }
        }
        
    }

    public RondelChoice[] getRondel() {
        return rondel;
    }

    public int nextTick() {
        Platform.exitNestedEventLoop(EVENTKEY, null);
        return tickCount;
    }

    public void removeUnitGUI(int location, int nation, UnitType type) {
        if(controllerGUI != null) controllerGUI.removeUnit(nation, location, type);
    }

    private void updateFactoriesInMap() {
        map.updateFields(nations);
    }

    private void updateBondOwners() {
        ArrayList<ArrayList<Bond>> ownedBondsPlayers = new ArrayList<>();
        for(int i = 0; i < playerCount; i++) ownedBondsPlayers.add(new ArrayList<Bond>());   
        for(int i = 0; i < AUbonds.length; i++) {
            if(AUbonds[i].getOwner() != null) {
                int ownerIndex = AUbonds[i].getOwner().getIndex();
                AUbonds[i].setPlayer(players[ownerIndex]);
                ownedBondsPlayers.get(ownerIndex).add(AUbonds[i]);
            }
            if(ITbonds[i].getOwner() != null) {
                int ownerIndex = ITbonds[i].getOwner().getIndex();
                ITbonds[i].setPlayer(players[ownerIndex]);
                ownedBondsPlayers.get(ownerIndex).add(ITbonds[i]);
            }
            if(FRbonds[i].getOwner() != null) {
                int ownerIndex = FRbonds[i].getOwner().getIndex();
                FRbonds[i].setPlayer(players[ownerIndex]);
                ownedBondsPlayers.get(ownerIndex).add(FRbonds[i]);
            }
            
            if(GBbonds[i].getOwner() != null) {
                int ownerIndex = GBbonds[i].getOwner().getIndex();
                GBbonds[i].setPlayer(players[ownerIndex]);
                ownedBondsPlayers.get(ownerIndex).add(GBbonds[i]);
            }
            
            if(GEbonds[i].getOwner() != null) {
                int ownerIndex = GEbonds[i].getOwner().getIndex();
                GEbonds[i].setPlayer(players[ownerIndex]);
                ownedBondsPlayers.get(ownerIndex).add(GEbonds[i]);
            }
            
            if(RUbonds[i].getOwner() != null) {
                int ownerIndex = RUbonds[i].getOwner().getIndex();
                RUbonds[i].setPlayer(players[ownerIndex]);
                ownedBondsPlayers.get(ownerIndex).add(RUbonds[i]);
            }
        }
        for(int i = 0; i < players.length; i++) {
            players[i].setOwnedBonds(ownedBondsPlayers.get(i));
        }
    }

    public void recordMapPositions() {
        try {
            FileWriter file = new FileWriter("Imperial/src/main/java/Group12/Imperial/testing/MapHistory.txt", true);
            PrintWriter writer = new PrintWriter(file);
            writer.println("______________________________________________________________");
            MapLocation[] allLocations = map.getAllMapLocations();
            for(int i = 0; i < map.getAllMapLocations().length; i++) {
                writer.println(allLocations[i].getIndex() + ": " + "| Units: " + allLocations[i].getUnitsAsString() + " | Owner: " + allLocations[i].getOwner() + " | Type: " + allLocations[i].getType());
            }
            writer.println("______________________________________________________________\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
