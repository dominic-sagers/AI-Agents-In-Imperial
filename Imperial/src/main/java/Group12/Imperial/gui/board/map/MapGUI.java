package Group12.Imperial.gui.board.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import Group12.Imperial.gamelogic.gameboard.Factory.FactoryType;
import Group12.Imperial.gamelogic.gameboard.Unit.UnitType;
import Group12.Imperial.gui.EventKey;
import Group12.Imperial.gui.GameScreen;
import Group12.Imperial.gui.PathContainer;
import Group12.Imperial.gui.board.listener.MapElementListener;
import Group12.Imperial.gui.board.listener.MapElementListener.Event;
import Group12.Imperial.gui.board.rondel.RondelGUI.RondelChoice;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class MapGUI implements Serializable{

    private MapElementGUI[] elements;
    private Rectangle[] flags;
    private MapElementListener listener;
    private Parent map;
    private GameScreen game;
    private Group mapGroup;

    private long lastEvent;
    private UnitGUI lastEventUnit;
    private MapElementGUI lastElement;
    private boolean unitWasMoved = false;
    private int phase;
    private UnitType importType;

    private final EventKey EVENTKEY = new EventKey();

    public MapGUI(GameScreen game) {
        this.game = game;
        this.listener = new MapElementListener(this);
        this.elements = new MapElementGUI[55];
        this.flags = new Rectangle[55];

        createGameBoard();

        mapGroup = new Group(getPaths());
        

        map = createZoomPane(mapGroup);
    }

    public Parent getMap() {
        return map;
    }

    private Parent createZoomPane(Group group) {
        final double SCALE_DELTA = 1.02;
        final StackPane zoomPane = new StackPane();

        zoomPane.getChildren().add(group);

        final Group zoomContent = new Group(zoomPane);
        final StackPane canvasPane = new StackPane();
        canvasPane.getChildren().add(zoomContent);

        final ScrollPane scroller = new ScrollPane();
        final Group scrollContent = new Group(canvasPane);
        scroller.setContent(scrollContent);

        scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                canvasPane.setMinSize(newValue.getWidth(), newValue.getHeight());
            }
        });

        scroller.setPrefViewportWidth(256);
        scroller.setPrefViewportHeight(256);

        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) return;

                double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

                Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);
                group.setScaleX(group.getScaleX() * scaleFactor);
                group.setScaleY(group.getScaleY() * scaleFactor);

                repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);
            }
        });

        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
        scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
            }
        });

        scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double deltaX = event.getX() - lastMouseCoordinates.get().getX();
                double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
                double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
                double desiredH = scroller.getHvalue() - deltaH;
                scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

                double deltaY = event.getY() - lastMouseCoordinates.get().getY();
                double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
                double deltaV = deltaY * (scroller.getHmax() - scroller.getHmin()) / extraHeight;
                double desiredV = scroller.getVvalue() - deltaV;
                scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
            }
        });

        return scroller;
    }

    private void repositionScroller(Group scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            double halfWidth = scroller.getViewportBounds().getWidth() / 2 ;
            double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            double halfHeight = scroller.getViewportBounds().getHeight() / 2 ;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }

    private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private Point2D[] readPosFile() {
        Point2D[] positions = new Point2D[55];

        try {
            Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("positions_map.csv"));
            int counter = 0;
            while(scanner.hasNextLine()) {
                Scanner row = new Scanner(scanner.nextLine());
                row.useDelimiter(",");
                positions[counter] = new Point2D(Double.parseDouble(row.next()), Double.parseDouble(row.next()));
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }

    private double[][] readObjectPosFile() {
        double[][] positions = new double[55][10];
        try {
            Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("positions_map_objects.csv"));
            int counter = 0;
            while(scanner.hasNextLine()) {
                Scanner row = new Scanner(scanner.nextLine());
                row.useDelimiter(",");
                positions[counter][0] = Double.parseDouble(row.next());
                positions[counter][1] = Double.parseDouble(row.next());
                positions[counter][2] = Double.parseDouble(row.next());
                positions[counter][3] = Double.parseDouble(row.next());
                positions[counter][4] = Double.parseDouble(row.next());
                positions[counter][5] = Double.parseDouble(row.next());
                positions[counter][6] = Double.parseDouble(row.next());
                positions[counter][7] = Double.parseDouble(row.next());
                positions[counter][8] = Double.parseDouble(row.next());
                positions[counter][9] = Double.parseDouble(row.next());
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }

    private void createGameBoard() {
        Point2D[] pos = readPosFile();
        double[][] objPos = readObjectPosFile();

        elements[0] = new MapElementGUI(0, PathContainer.wa1, Color.AQUA, pos[0], objPos[0], null, listener);
        elements[1] = new MapElementGUI(1, PathContainer.wa2, Color.AQUA, pos[1], objPos[1], null, listener);
        elements[2] = new MapElementGUI(2, PathContainer.wa3, Color.AQUA, pos[2], objPos[2], null, listener);
        elements[3] = new MapElementGUI(3, PathContainer.wa4, Color.AQUA, pos[3], objPos[3], null, listener);
        elements[4] = new MapElementGUI(4, PathContainer.wa5, Color.AQUA, pos[4], objPos[4], null, listener);
        elements[5] = new MapElementGUI(5, PathContainer.wa6, Color.AQUA, pos[5], objPos[5], null, listener);
        elements[6] = new MapElementGUI(6, PathContainer.wa7, Color.AQUA, pos[6], objPos[6], null, listener);
        elements[7] = new MapElementGUI(7, PathContainer.wa8, Color.AQUA, pos[7], objPos[7], null, listener);
        elements[8] = new MapElementGUI(8, PathContainer.wa9, Color.AQUA, pos[8], objPos[8], null, listener);

        elements[9]  = new MapElementGUI(9,  PathContainer.uk1, Color.ORANGERED, pos[9], objPos[9], FactoryType.BLUE, listener);
        elements[10] = new MapElementGUI(10, PathContainer.uk2, Color.ORANGERED, pos[10], objPos[10], FactoryType.BLUE, listener);
        elements[11] = new MapElementGUI(11, PathContainer.uk3, Color.ORANGERED, pos[11], objPos[11], FactoryType.BLUE, listener);
        elements[12] = new MapElementGUI(12, PathContainer.uk4, Color.ORANGERED, pos[12], objPos[12], FactoryType.BROWN, listener);
        elements[13] = new MapElementGUI(13, PathContainer.uk5, Color.ORANGERED, pos[13], objPos[13], FactoryType.BLUE, listener);

        elements[14] = new MapElementGUI(14, PathContainer.sw1, Color.BEIGE, pos[14], objPos[14], null, listener);
        elements[15] = new MapElementGUI(15, PathContainer.sw2, Color.BEIGE, pos[15], objPos[15], null, listener);
        elements[16] = new MapElementGUI(16, PathContainer.sw3, Color.BEIGE, pos[16], objPos[16], null, listener);
        elements[17] = new MapElementGUI(17, PathContainer.sw4, Color.BEIGE, pos[17], objPos[17], null, listener);
        elements[18] = new MapElementGUI(18, PathContainer.sw5, Color.BEIGE, pos[18], objPos[18], null, listener);

        elements[19] = new MapElementGUI(19, PathContainer.fr1, Color.BLUE, pos[19], objPos[19], FactoryType.BROWN, listener);
        elements[20] = new MapElementGUI(20, PathContainer.fr2, Color.BLUE, pos[20], objPos[20], FactoryType.BLUE, listener);
        elements[21] = new MapElementGUI(21, PathContainer.fr3, Color.BLUE, pos[21], objPos[21], FactoryType.BROWN, listener);
        elements[22] = new MapElementGUI(22, PathContainer.fr4, Color.BLUE, pos[22], objPos[22], FactoryType.BLUE, listener);
        elements[23] = new MapElementGUI(23, PathContainer.fr5, Color.BLUE, pos[23], objPos[23], FactoryType.BLUE, listener);

        elements[24] = new MapElementGUI(24, PathContainer.be, Color.BEIGE, pos[24], objPos[24], null, listener);
        elements[25] = new MapElementGUI(25, PathContainer.nl, Color.BEIGE, pos[25], objPos[25], null, listener);
        elements[26] = new MapElementGUI(26, PathContainer.ch, Color.LIGHTGRAY, pos[26], objPos[26], null, listener);
        elements[27] = new MapElementGUI(27, PathContainer.no, Color.BEIGE, pos[27], objPos[27], null, listener);
        elements[28] = new MapElementGUI(28, PathContainer.se, Color.BEIGE, pos[28], objPos[28], null, listener);
        elements[29] = new MapElementGUI(29, PathContainer.de, Color.BEIGE, pos[29], objPos[29], null, listener);

        elements[30] = new MapElementGUI(30, PathContainer.ge1, Color.DARKGRAY, pos[30], objPos[30], FactoryType.BLUE, listener);
        elements[31] = new MapElementGUI(31, PathContainer.ge2, Color.DARKGRAY, pos[31], objPos[31], FactoryType.BROWN, listener);
        elements[32] = new MapElementGUI(32, PathContainer.ge3, Color.DARKGRAY, pos[32], objPos[32], FactoryType.BLUE, listener);
        elements[33] = new MapElementGUI(33, PathContainer.ge4, Color.DARKGRAY, pos[33], objPos[33], FactoryType.BROWN, listener);
        elements[34] = new MapElementGUI(34, PathContainer.ge5, Color.DARKGRAY, pos[34], objPos[34], FactoryType.BROWN, listener);

        elements[35] = new MapElementGUI(35, PathContainer.it1, Color.GREEN, pos[35], objPos[35], FactoryType.BLUE, listener);
        elements[36] = new MapElementGUI(36, PathContainer.it2, Color.GREEN, pos[36], objPos[36], FactoryType.BLUE, listener);
        elements[37] = new MapElementGUI(37, PathContainer.it3, Color.GREEN, pos[37], objPos[37], FactoryType.BROWN, listener);
        elements[38] = new MapElementGUI(38, PathContainer.it4, Color.GREEN, pos[38], objPos[38], FactoryType.BROWN, listener);
        elements[39] = new MapElementGUI(39, PathContainer.it5, Color.GREEN, pos[39], objPos[39], FactoryType.BLUE, listener);

        elements[40] = new MapElementGUI(40, PathContainer.ah1, Color.YELLOW, pos[40], objPos[40], FactoryType.BROWN, listener);
        elements[41] = new MapElementGUI(41, PathContainer.ah2, Color.YELLOW, pos[41], objPos[41], FactoryType.BROWN, listener);
        elements[42] = new MapElementGUI(42, PathContainer.ah3, Color.YELLOW, pos[42], objPos[42], FactoryType.BROWN, listener);
        elements[43] = new MapElementGUI(43, PathContainer.ah4, Color.YELLOW, pos[43], objPos[43], FactoryType.BROWN, listener);
        elements[44] = new MapElementGUI(44, PathContainer.ah5, Color.YELLOW, pos[44], objPos[44], FactoryType.BLUE, listener);

        elements[45] = new MapElementGUI(45, PathContainer.ru1, Color.PURPLE, pos[45], objPos[45], FactoryType.BLUE, listener);
        elements[46] = new MapElementGUI(46, PathContainer.ru2, Color.PURPLE, pos[46], objPos[46], FactoryType.BROWN, listener);
        elements[47] = new MapElementGUI(47, PathContainer.ru3, Color.PURPLE, pos[47], objPos[47], FactoryType.BROWN, listener);
        elements[48] = new MapElementGUI(48, PathContainer.ru4, Color.PURPLE, pos[48], objPos[48], FactoryType.BROWN, listener);
        elements[49] = new MapElementGUI(49, PathContainer.ru5, Color.PURPLE, pos[49], objPos[49], FactoryType.BLUE, listener);

        elements[50] = new MapElementGUI(50, PathContainer.se1, Color.BEIGE, pos[50], objPos[50], null, listener);
        elements[51] = new MapElementGUI(51, PathContainer.se2, Color.BEIGE, pos[51], objPos[51], null, listener);
        elements[52] = new MapElementGUI(52, PathContainer.se3, Color.BEIGE, pos[52], objPos[52], null, listener);
        elements[53] = new MapElementGUI(53, PathContainer.se4, Color.BEIGE, pos[53], objPos[53], null, listener);
        elements[54] = new MapElementGUI(54, PathContainer.se5, Color.BEIGE, pos[54], objPos[54], null, listener);
    }

    private SVGPath[] getPaths() {
        SVGPath[] paths = new SVGPath[elements.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = elements[i].getElement();
        }
        return paths;
    }

    public void eventHappenedElement(MapElementGUI element, Event event) {
        if (event == Event.CLICK) {
            if(phase == 1) {
                if (game.isFactoryBuildLegal(element.getIndex())) {
                    mapGroup.getChildren().add(element.addFactory());
                    Platform.exitNestedEventLoop(EVENTKEY, element.getIndex());
                }
            } else if(phase == 2) {
                if (game.isImportLegal(element.getIndex(), importType)) {
                    Platform.exitNestedEventLoop(EVENTKEY, element.getIndex());
                }
            }
            
        } else if (event == Event.RELEASED) {
            long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - lastEvent);
            if (elapsedTime < 10) {
                lastElement = element;
                if (element == lastEventUnit.getLastElement()) {
                    lastEventUnit.setToLastPosition();
                } else if (game.isManeuverLegal(lastEventUnit.getLastLocation(), element.getIndex(), lastEventUnit.getType(), lastEventUnit.getNumber())) {
                    //if(!unitWasMoved) element.addUnit(lastEventUnit);
                    //unitWasMoved = false;
                } else {
                    lastEventUnit.setToLastPosition();
                }
            }
        }
        
    }

    public void eventHappenedUnit(UnitGUI unit, Event event) {
        lastEvent = System.nanoTime();
        lastEventUnit = unit;
    }

    public void getManeuver(int phase) {
        this.phase = phase;
        Platform.enterNestedEventLoop(EVENTKEY);
    }

    public void finishedManeuver() {
        Platform.exitNestedEventLoop(EVENTKEY, null);
    }

    public void addUnit(int nation, int number, int elementIndex, UnitType type) {
        UnitGUI unit = new UnitGUI(type, new Point2D(0, 0), number, nation, 10, elementIndex, listener);
        mapGroup.getChildren().add(unit.getUnit());
        elements[elementIndex].addUnit(unit);
    }

    public void moveUnitAfterManeuver(int nation, UnitType type, int fromLocation, int toLocation) {
        //unitWasMoved = true;
        ArrayList<UnitGUI> units = elements[fromLocation].getUnits();
        UnitGUI unitToMove = null;
        
        for(UnitGUI unit : units) {
            if(unit.getNation() == nation && unit.getType() == type) unitToMove = unit;
        }
        if(unitToMove == null) {
            System.out.println("No unit found that matches the description");
            System.out.println("UnitType: " + type + ", Nation: " + nation + ", From Location: " + fromLocation + ", To Location: " + toLocation);
        }   
        elements[toLocation].addUnit(unitToMove);
    }

    public void removeUnit(int nation, int location, UnitType type) {
        UnitGUI unit = elements[location].removeUnit(nation, type);
        removeFromGroup(unit.getUnit());
    }

    public void addToGroup(Group unit) {
        mapGroup.getChildren().add(unit);
    }

    public void removeFromGroup(Group unit) {
        mapGroup.getChildren().remove(unit);
    }

    public int buildFactory(int phase) {
        this.phase = phase;
        return (int)Platform.enterNestedEventLoop(EVENTKEY);
    }

    public void buildInitialFactory(int location) {
        mapGroup.getChildren().add(elements[location].addFactory());
    }

    public void addFlag(int nation, int elementIndex) {
        Rectangle flag = elements[elementIndex].addFlag(nation);
        if(!mapGroup.getChildren().contains(flag)) mapGroup.getChildren().add(flag);
        
        flags[elementIndex] = flag;
    }

    public void removeFlag(int elementIndex) {
        mapGroup.getChildren().remove(flags[elementIndex]);  
    }

    public void lockMap() {
        for (MapElementGUI element : elements) {
            element.lock();
        }
    }

    public void unlockMap(int nation, RondelChoice choice) {
        for (MapElementGUI element : elements) {
            element.unlock(nation, choice);
        }
    }

    public void updateUnit(int nation, int location, int strength) {
        elements[location].updateUnit(nation, strength);
    }

    public int getUnitImportLocation(UnitType type) {
        importType = type;
        phase = 2;
        return (int)Platform.enterNestedEventLoop(EVENTKEY);
        
    }
}
