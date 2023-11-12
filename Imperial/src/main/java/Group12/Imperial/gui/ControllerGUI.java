package Group12.Imperial.gui;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Player.PlayerType;
import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gui.board.rondel.RondelGUI.RondelChoice;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControllerGUI implements Serializable{

    private int numberPlayers;
    private Controller controller;
    private Scene scene;
    private String[] playerNames;
    private PlayerType[] playerTypes;

    private GameScreen gameScreen;
    
    public ControllerGUI(int numberPlayers, Scene scene, PlayerType[] playerTypes, String[] playerNames, Stage stage) {
        this.numberPlayers = numberPlayers;
        this.scene = scene;
        this.playerNames = playerNames;
        this.playerTypes = playerTypes;
        this.gameScreen = new GameScreen(this, stage);
        
    }

    public void start() throws InterruptedException {
        this.gameScreen.load(numberPlayers, playerNames, scene);
        this.controller = new Controller(this, numberPlayers, playerTypes, true);
        controller.start();
    }

    public void startSimulation() throws InterruptedException {
        gameScreen.addSimulationOption();
        this.gameScreen.load(numberPlayers, playerNames, scene);
        this.controller = new Controller(this, numberPlayers, playerTypes, true);
        controller.startSimulationGUI();
    }

    public int makeRondelChoice(int currentPlayer, int currentNation) {
        gameScreen.lockMap();
        return gameScreen.getRondelChoice(currentPlayer, currentNation);
    }

    public void makeManeuverChoice(int currentPlayer, int currentNation) {
        gameScreen.lockMap();
        gameScreen.unlockMap(currentNation, RondelChoice.MANEUVER);
        gameScreen.getManeuver(currentPlayer, currentNation);
    }

    public int makeFactoryChoice(int currentPlayer, int currentNation) {
        gameScreen.lockMap();
        gameScreen.unlockMap(currentNation, RondelChoice.FACTORY);
        return gameScreen.buildFactory(currentPlayer, currentNation);
    }

    public void addUnit(int currentPlayer, int currentNation, int element, int type, boolean shouldUpdateMenuBar) {
        gameScreen.lockMap();
        gameScreen.addUnit(currentPlayer, currentNation, element, type, shouldUpdateMenuBar);
    }

    public void removeUnit(int nation, int location, UnitType type) {
        gameScreen.removeUnit(nation, location, type);
    }

    public void makeImportChoice(int currentPlayer, int currentNation) {
        gameScreen.lockMap();
        gameScreen.unlockMap(currentNation, RondelChoice.IMPORT);
        gameScreen.makeImportChoice(currentPlayer, currentNation);
    }

    public boolean isManeuverLegal(int fromElement, int toElement, UnitType unitType, int amount) {
        return controller.isManeuverLegal(fromElement, toElement, unitType, amount);
    }

    public boolean isFactoryBuildLegal(int index) {
        return controller.isFactoryBuildLegal(index);
    }

    public void buildInitialFactory(int location) {
        gameScreen.buildInitialFactory(location);
    }

    public void updateNationScore(int nation, int score) {
        gameScreen.updateScoringTrack(nation, score);
    }

    public void updateNationTreasury(int nation, int money) {
        gameScreen.updateNationTreasury(nation, money);
    }

    public void updateTaxChart(int nation, int position) {
        gameScreen.updateTaxationChart(nation, position);
    }

    public void updatePlayerTreasury(int playerIndex, int money, String[] bonds) {
        gameScreen.updatePlayerTreasury(playerIndex, money, bonds);
    }

    public void updateUnit(int nation, int location, int strength, UnitType unitType) {
        gameScreen.updateUnit(nation, location, strength);
    }

    public boolean makeIsHostileChoice(int currentPlayer){
        return gameScreen.makeIsHostileChoice(currentPlayer);
    }

    public int makeBattleChoice(int currentPlayer, int currentNation, int location, ArrayList<Integer> nationsPresent) {
        return gameScreen.makeBattleChoice(currentPlayer, currentNation, location, nationsPresent);
    }

    public void moveUnitAfterManeuver(int nation, UnitType type, int fromLocation, int toLocation) {
        gameScreen.moveUnitAfterManeuver(nation, type, fromLocation, toLocation);
    }

    public void finishedImport() {
        controller.finishedImport();
    }

    public boolean isImportLegal(int index, UnitType importType) {
        return controller.isImportLegal(index, importType);
    }
    
    public boolean isRondelMoveLegal(int rondelIndex) {
        return controller.isRondelMoveLegal(rondelIndex);
    }

    public int[] makeBondBuyChoice(int currentPlayer, int currentNation, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCost) {
        return gameScreen.makeBondBuyChoice(currentPlayer, currentNation, possibleActions, associatedCost);
    }
    
    public void addFlag(int nation, int elementIndex) {
        gameScreen.addFlag(nation, elementIndex);
    }

    public void removeFlag(int elementIndex){
        gameScreen.removeFlag(elementIndex);
    }

    public int nextTick() {
        return controller.nextTick();
    }

    public void updateMenuBar(int currentPlayer, int currentNation, String phase, boolean maneuver) {
        gameScreen.updateMenuBar(currentPlayer, currentNation, phase, maneuver);
    }

    public void updateRondelManually(int currentPlayer, int nation, int position) {
        gameScreen.updateRondelManually(currentPlayer, nation, position);
    }
}
