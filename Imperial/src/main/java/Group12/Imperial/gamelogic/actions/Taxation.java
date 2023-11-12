package Group12.Imperial.gamelogic.actions;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.Controller;
import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.gameboard.Factory;
import Group12.Imperial.gamelogic.gameboard.TaxChart;
import Group12.Imperial.gamelogic.gameboard.Unit;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gamelogic.gameboard.map.Map;

public class Taxation implements Serializable{

    public static void taxation(Controller controller, Map map, Nation nation, TaxChart taxChart) {
        ArrayList<Integer> ownedLocations = nation.getOwnedLocations();
        ArrayList<Factory> ownedFactories = nation.getFactories();

        int locationCounter = 0;
        for (Integer location : ownedLocations) {
            if (!map.getLocation(location.intValue()).isHomeProvince())
                locationCounter++;
        }

        int factoryCounter = 0;

        for (Factory factory : ownedFactories) {
            boolean occupied = false;
            ArrayList<Integer> nationsPresent = map.getLocation(factory.getLocationIndex()).getNationsPresent();
            for (Integer nationPre : nationsPresent) {
                if (nationPre.intValue() != nation.getIndex()) {
                    occupied = true;
                }
            }
            if (!occupied)
                factoryCounter++;
        }
        int taxes = factoryCounter * 2 + locationCounter;
        int bonus = taxChart.repositionNation(nation.getIndex(), taxes);
        controller.payPlayer(bonus);
        boolean finishedGame = nation.addPowerPoints(taxChart.getNationPosition(nation.getIndex()));
        

        ArrayList<Integer> unitLocations = nation.getUnitLocations();
        int unitPay = 0;
        for(Integer unitLocation : unitLocations) {
            Unit unitShip = map.getLocation(unitLocation).getUnit(nation.getIndex(), UnitType.SHIP);
            if(unitShip != null) unitPay += unitShip.getStrength();
            
            Unit unitArmy = map.getLocation(unitLocation).getUnit(nation.getIndex(), UnitType.ARMY);
            if(unitArmy != null) unitPay += unitArmy.getStrength();
        }
        if ((taxes - unitPay) > 0) {
            nation.addMoney(taxes - unitPay);
        }
        if (finishedGame) { 
            controller.gameFinished();
        }
    }

}
