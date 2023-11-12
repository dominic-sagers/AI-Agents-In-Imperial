package Group12.Imperial.gamelogic.gameboard.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import Group12.Imperial.gamelogic.Nation;
import Group12.Imperial.gamelogic.gameboard.Factory;
import Group12.Imperial.gamelogic.gameboard.Unit;
import Group12.Imperial.gamelogic.gameboard.Factory.FactoryType;
import Group12.Imperial.gamelogic.gameboard.map.MapLocation.LocationType;

public class Map implements Serializable{

    private MapLocation[] locations;
    private Nation[] nations;

    /**
     *  Creates the game map
     * @param nations The nations of the game
     */
    public Map(Nation[] nations) {
        this.nations = nations;
        init();
    }

    public MapLocation getLocation(int location) {
        return locations[location];
    }

    public MapLocation[] getAllMapLocations(){return locations;}

    /**
     *  Initialises the MapLocations by reading in the "location_init.csv" file
     */
    private void init() {
        locations = new MapLocation[55];
        for(int i = 0; i < locations.length; i++) {
            locations[i] = new MapLocation(i);
        }

        int counter = 0;
        Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("location_init.csv"));
        while(scanner.hasNextLine()) {
            Scanner row = new Scanner(scanner.nextLine());
            row.useDelimiter(",");

            LocationType type = null;
            ArrayList<MapLocation> neighbours = new ArrayList<>();
            Nation owner = null;
            boolean isHomeProvince = false;
            FactoryType factoryType = null;
            int currentPos = 0;
            while(row.hasNext()) {
                
                String value = row.next();
                if(value.equals("end")) {
                    currentPos++; 
                } else {
                    if(currentPos == 0) {
                        if (value.equals("0")) { type = LocationType.SEA; 
                        } else { type = LocationType.LAND; }
                    } else if(currentPos == 1) {
                        neighbours.add(locations[Integer.parseInt(value)]);
                    } else if(currentPos  == 2) {
                        owner = nations[Integer.parseInt(value)];
                    } else if(currentPos == 3) {
                        if(value.equals("1")) {
                            isHomeProvince = true;
                        }
                    } else if(currentPos == 4) {
                        if(value.equals("0")) { factoryType = FactoryType.BLUE; 
                        } else { factoryType = FactoryType.BROWN; }
                    }
                }
            }        
            locations[counter].init(type, neighbours, owner, isHomeProvince, factoryType);
            counter++;
        }
    }

    public void updateFields(Nation[] nations) {
        this.nations = nations;
        for(MapLocation mapLocation : locations) {
            if(mapLocation.getOwner() != null) {
                mapLocation.setOwner(nations[mapLocation.getOwner().getIndex()]);
            }
            for(Unit u : mapLocation.getUnits()) {
                if(u != null) {
                    u.setOwner(nations[u.getOwner().getIndex()]);
                }
            }
        }
        for(Nation nation : nations) {
            for(Factory fac : nation.getFactories()) {
                locations[fac.getLocationIndex()].buildFactory(fac);
                fac.setLocation(locations[fac.getLocationIndex()]);
            }
        }
    }
    
}
