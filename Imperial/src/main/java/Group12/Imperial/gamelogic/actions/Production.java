package Group12.Imperial.gamelogic.actions;

import java.io.Serializable;
import java.util.ArrayList;

import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.gameboard.Factory;
import Group12.Imperial.gamelogic.gameboard.Factory.FactoryType;
import Group12.Imperial.gamelogic.gameboard.map.Map;

public class Production implements Serializable{

    public static ArrayList<ArrayList<Integer>> produce(Map map, Nation nation) {
        ArrayList<ArrayList<Integer>> output = new ArrayList<>();
        //System.out.println("In the method produce");
        ArrayList<Factory> factories = nation.getFactories();

        for (Factory factory : factories) {
            ArrayList<Integer> factoryOutput = new ArrayList<>();
            boolean canProduce = true;
            ArrayList<Integer> nationsPresent = map.getLocation(factory.getLocationIndex()).getNationsPresent();
            for (Integer i : nationsPresent) {
                if (i != nation.getIndex())
                    canProduce = false;
            }
            if (canProduce) {
                factory.produce(1);
                nation.addUnitLocation(factory.getLocationIndex());
                factoryOutput.add(Integer.valueOf(factory.getLocationIndex()));
                if (factory.getType() == FactoryType.BROWN) {
                    factoryOutput.add(Integer.valueOf(0));
                } else {
                    factoryOutput.add(Integer.valueOf(1));
                }
                output.add(factoryOutput);
            }
        }

        return output;
    }

}
