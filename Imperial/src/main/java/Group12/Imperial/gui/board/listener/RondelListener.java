package Group12.Imperial.gui.board.listener;

import java.io.Serializable;

import Group12.Imperial.gui.board.listener.MapElementListener.Event;
import Group12.Imperial.gui.board.rondel.NationPieceRondelGUI;
import Group12.Imperial.gui.board.rondel.RondelElement;
import Group12.Imperial.gui.board.rondel.RondelGUI;

public class RondelListener implements Serializable{
    private RondelGUI rondel;

    public RondelListener(RondelGUI rondel) {
        this.rondel = rondel;
    }

    public void wasReleased(RondelElement element) {
        rondel.eventHappenedElement(element, Event.RELEASED);
    }

    public void dragEnded(NationPieceRondelGUI playerPieceGUI) {
        rondel.eventHappenedPlayer(playerPieceGUI, Event.DRAG);
    }

    public void wasReleased() {
        rondel.eventHappenedElement(null, Event.DRAG);
    }
}
