package Group12.Imperial.gamelogic.actions;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Player;
import Group12.Imperial.gamelogic.agents.GameState;
import Group12.Imperial.gamelogic.gameboard.Bond;
import Group12.Imperial.gamelogic.Nation;

import java.io.Serializable;
import java.util.ArrayList;

public class Investor implements Serializable{


    public static void makeInvestorChoice(Controller controller, Player[] players, Nation[] nations) throws InterruptedException {
        for (Player player : players) {
            ArrayList<Bond> playerOwnedBonds = player.getOwnedBonds();
            for (Bond playerOwnedBond : playerOwnedBonds) {
                for (Nation nation : nations) {
                    if (nation.getName() == playerOwnedBond.getNationName()) {
                        int ownerOfNation = controller.getOwnerOfNation(nation.getIndex());
                        int treasuryOfNation = nation.getTreasury();
                        if (treasuryOfNation >= playerOwnedBond.getInterest()) {
                            player.addMoney(playerOwnedBond.getInterest());
                            nation.reduceMoney(playerOwnedBond.getInterest());
                        } else {
                            player.addMoney(playerOwnedBond.getInterest());
                            players[ownerOfNation].reduceMoney(playerOwnedBond.getInterest());
                        }
                    }
                }
            }
        }
        investorWasPassed(controller, players);
    }

    public static void investorWasPassed(Controller controller, Player[] players) throws InterruptedException {
        for (Player player : players) {
            if (player.hasInvestor()) {
                player.addMoney(2);

                BondOptions bondOptions = getPossibleBonds(controller, player);
                int[] bondChoice = controller.makeBondBuyChoice(player, bondOptions.possibleActions, bondOptions.associatedCost);
                ArrayList<Bond> ownedBonds = player.getOwnedBonds();
                

                if(bondChoice[0] == -1) return;
                Bond bondToAdd = bondOptions.possibleActions.get(bondChoice[0]);
                int valueOfBondToUpgrade = bondOptions.associatedCost.get(bondChoice[0]).get(bondChoice[1]);
                
                if (valueOfBondToUpgrade == -1) {
                    bondToAdd.setPlayer(player);
                    player.addBond(bondToAdd);
                    player.reduceMoney(bondToAdd.getValue());
                } else {
                    bondToAdd.setPlayer(player);
                    player.addBond(bondToAdd);
                    ArrayList<Bond> bondsToRemove = new ArrayList<>();
                    for (Bond b : ownedBonds) {
                        if (bondToAdd.getNationName() == b.getNationName() && b.getValue() == valueOfBondToUpgrade) {
                            b.setPlayer(null);
                            bondsToRemove.add(b);
                        }
                    }
                    for(Bond bond : bondsToRemove) {
                        player.removeBond(bond);
                    }
                    player.reduceMoney(bondToAdd.getValue() - valueOfBondToUpgrade);
                }

            }
        }
        

        int[][] bondValuesAllPlayers = new int[6][players.length];

        for(Player player : players) {
            ArrayList<Bond> allownedBonds = player.getOwnedBonds();
            for(Bond bond : allownedBonds) {
                bondValuesAllPlayers[bond.getNationName().index][player.getIndex()] += bond.getValue();
            }
        }
        int[] ownerShipOfNation = new int[6];
        int[] numberOfBonds = new int[6];
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < players.length; j++) {
                if(bondValuesAllPlayers[i][j] > numberOfBonds[i]) {
                    numberOfBonds[i] = bondValuesAllPlayers[i][j];
                    ownerShipOfNation[i] = j;
                }
            }
        }
        for(int i = 0; i < ownerShipOfNation.length; i++) {
            int playerIndexOldOwner = controller.getOwnerOfNation(i);
            if(ownerShipOfNation[i] != playerIndexOldOwner) {
                players[ownerShipOfNation[i]].addNation(controller.getNationOnIndex(i));
                players[playerIndexOldOwner].removeNation(controller.getNationOnIndex(i));
            }
        }

        boolean nextPlayerHasInvestor = false;
        for(int i = 0; i < players.length; i++) {
            if(players[i].hasInvestor()) {
                players[i].setInvestor(false);
                nextPlayerHasInvestor = true;
            } else if(nextPlayerHasInvestor) {
                players[i].setInvestor(true);
                nextPlayerHasInvestor = false;
            }
        }
        if(nextPlayerHasInvestor) {
            players[0].setInvestor(true);
        }
    }

    public static void manualInvestorWasPassed(GameState gameState, ArrayList<Bond> possibleActions, ArrayList<ArrayList<Integer>> associatedCost, int[] bondChoice) {
        Player[] players = gameState.getPlayers();
        Player player = players[1];
        ArrayList<Bond> ownedBonds = player.getOwnedBonds();
        if(bondChoice[0] == -1) return;
        Bond bondToAdd = possibleActions.get(bondChoice[0]);
        int valueOfBondToUpgrade = associatedCost.get(bondChoice[0]).get(bondChoice[1]);
        
        if (valueOfBondToUpgrade == -1) {
            bondToAdd.setPlayer(player);
            player.addBond(bondToAdd);
            player.reduceMoney(bondToAdd.getValue());
        } else {
            bondToAdd.setPlayer(player);
            player.addBond(bondToAdd);
            ArrayList<Bond> bondsToRemove = new ArrayList<>();
            for (Bond b : ownedBonds) {
                if (bondToAdd.getNationName() == b.getNationName() && b.getValue() == valueOfBondToUpgrade) {
                    b.setPlayer(null);
                    bondsToRemove.add(b);
                }
            }
            for(Bond bond : bondsToRemove) {
                player.removeBond(bond);
            }
            player.reduceMoney(bondToAdd.getValue() - valueOfBondToUpgrade);
        }
        int[][] bondValuesAllPlayers = new int[6][players.length];

        for(Player p: players) {
            ArrayList<Bond> allownedBonds = p.getOwnedBonds();
            for(Bond bond : allownedBonds) {
                bondValuesAllPlayers[bond.getNationName().index][p.getIndex()] += bond.getValue();
            }
        }
        int[] ownerShipOfNation = new int[6];
        int[] numberOfBonds = new int[6];
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < players.length; j++) {
                if(bondValuesAllPlayers[i][j] > numberOfBonds[i]) {
                    numberOfBonds[i] = bondValuesAllPlayers[i][j];
                    ownerShipOfNation[i] = j;
                }
            }
        }
        for(int i = 0; i < ownerShipOfNation.length; i++) {
            int playerIndexOldOwner = gameState.getController().getOwnerOfNation(i);
            if(ownerShipOfNation[i] != playerIndexOldOwner) {
                players[ownerShipOfNation[i]].addNation(gameState.getController().getNationOnIndex(i));
                players[playerIndexOldOwner].removeNation(gameState.getController().getNationOnIndex(i));
            }
        }

        boolean nextPlayerHasInvestor = false;
        for(int i = 0; i < players.length; i++) {
            if(players[i].hasInvestor()) {
                players[i].setInvestor(false);
                nextPlayerHasInvestor = true;
            } else if(nextPlayerHasInvestor) {
                players[i].setInvestor(true);
                nextPlayerHasInvestor = false;
            }
        }
        if(nextPlayerHasInvestor) {
            players[0].setInvestor(true);
        }
    }

    public static BondOptions getPossibleBonds(Controller controller, Player player) {
        ArrayList<Bond> possibleBonds = new ArrayList<>();
        ArrayList<ArrayList<Integer>> associatedCost = new ArrayList<>();
        ArrayList<Bond> ownedBonds = player.getOwnedBonds();
        ArrayList<Bond[]> allNationBonds = new ArrayList<>();
        int money = player.getMoney();

        allNationBonds.add(controller.AUbonds);
        allNationBonds.add(controller.ITbonds);
        allNationBonds.add(controller.FRbonds);
        allNationBonds.add(controller.GBbonds);
        allNationBonds.add(controller.GEbonds);
        allNationBonds.add(controller.RUbonds);

        for (Bond[] b : allNationBonds) {
            for (Bond i : b) {
                if (i.getOwner() == null) {
                    ArrayList<Integer> bondBuyOptions = getBondBuyOptions(i, ownedBonds, money);
                    if (bondBuyOptions.size() != 0) {
                        possibleBonds.add(i);
                        associatedCost.add(bondBuyOptions);
                    }
                }
            }
        }

        return new BondOptions(possibleBonds, associatedCost);
    }

    public static ArrayList<Integer> getBondBuyOptions(Bond bond, ArrayList<Bond> ownedBonds, int money) {
        ArrayList<Integer> buyOptions = new ArrayList<>();
        if (money >= bond.getValue()) {
            buyOptions.add(Integer.valueOf(-1));
            buyOptions.add(Integer.valueOf(bond.getValue()));
        }
        for (Bond b : ownedBonds) {
            if (b.getNationName() == bond.getNationName() && b.getValue() < bond.getValue() && (money - (bond.getValue() - b.getValue())) >= 0) {
                buyOptions.add(Integer.valueOf(b.getValue()));
                buyOptions.add(Integer.valueOf(bond.getValue() - b.getValue()));
            }
        }
        return buyOptions;
    }
}
