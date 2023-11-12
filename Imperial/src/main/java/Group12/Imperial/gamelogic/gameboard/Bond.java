package Group12.Imperial.gamelogic.gameboard;

import java.io.Serializable;

import Group12.Imperial.gamelogic.Player;
import Group12.Imperial.gamelogic.Nation.NationName;

public class Bond implements Serializable{
    public enum BondType implements Serializable {
        AU_2(NationName.AUSTRIAHUNGARY, 2, 1), AU_4(NationName.AUSTRIAHUNGARY, 4, 2), AU_6(NationName.AUSTRIAHUNGARY, 6, 3), AU_9(NationName.AUSTRIAHUNGARY, 9, 4), AU_12(NationName.AUSTRIAHUNGARY, 12, 5), AU_16(NationName.AUSTRIAHUNGARY, 16, 6), AU_20(NationName.AUSTRIAHUNGARY, 20, 7), AU_25(NationName.AUSTRIAHUNGARY, 25, 8),
        IT_2(NationName.ITALY, 2, 1), IT_4(NationName.ITALY, 4, 2), IT_6(NationName.ITALY, 6, 3), IT_9(NationName.ITALY, 9, 4), IT_12(NationName.ITALY, 12, 5), IT_16(NationName.ITALY, 16, 6), IT_20(NationName.ITALY, 20, 7), IT_25(NationName.ITALY, 25, 8),
        FR_2(NationName.FRANCE, 2, 1), FR_4(NationName.FRANCE, 4, 2), FR_6(NationName.FRANCE, 6, 3), FR_9(NationName.FRANCE, 9, 4), FR_12(NationName.FRANCE, 12, 5), FR_16(NationName.FRANCE, 16, 6), FR_20(NationName.FRANCE, 20, 7), FR_25(NationName.FRANCE, 25, 8),
        GB_2(NationName.GREATBRITAIN, 2, 1), GB_4(NationName.GREATBRITAIN, 4, 2), GB_6(NationName.GREATBRITAIN, 6, 3), GB_9(NationName.GREATBRITAIN, 9, 4), GB_12(NationName.GREATBRITAIN, 12, 5), GB_16(NationName.GREATBRITAIN, 16, 6), GB_20(NationName.GREATBRITAIN, 20, 7), GB_25(NationName.GREATBRITAIN, 25, 8),
        GE_2(NationName.GERMANY, 2, 1), GE_4(NationName.GERMANY, 4, 2), GE_6(NationName.GERMANY, 6, 3), GE_9(NationName.GERMANY, 9, 4), GE_12(NationName.GERMANY, 12, 5), GE_16(NationName.GERMANY, 16, 6), GE_20(NationName.GERMANY, 20, 7), GE_25(NationName.GERMANY, 25, 8),
        RU_2(NationName.RUSSIA, 2, 1), RU_4(NationName.RUSSIA, 4, 2), RU_6(NationName.RUSSIA, 6, 3), RU_9(NationName.RUSSIA, 9, 4), RU_12(NationName.RUSSIA, 12, 5), RU_16(NationName.RUSSIA, 16, 6), RU_20(NationName.RUSSIA, 20, 7), RU_25(NationName.RUSSIA, 25, 8);
    
        private NationName nationName;
        private int value;
        private int interest;

        private BondType(NationName nationName, int value, int interest) {
            this.nationName = nationName;
            this.value = value;
            this.interest = interest;
        }
    }
    
    private BondType bondType;
    private Player owner;

    public Bond(BondType bondType) {
        this.bondType = bondType;
    }

    @Override
    public boolean equals(Object bond) {
        Bond b = (Bond)bond;
        if(b.getNationName() == this.getNationName() && b.getValue() == this.getValue() && b.getOwner() == this.getOwner()) {
            return true;
        }
        return false;
    }

    public void setPlayer(Player player) { this.owner = player; }
    public Player getOwner() { return owner; }
    public NationName getNationName() { return bondType.nationName; }
    public int getValue() { return bondType.value; }
    public int getInterest() { return bondType.interest; }
    
    
}
