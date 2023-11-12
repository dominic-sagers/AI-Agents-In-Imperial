Group 12: Imperial Game
Members: Adelin Bîrzan, Fleur de Bruin, Johann Lottermoser, Dominic Sagers, Raf Tesema, Selin Yazici

Note: It's important before running any code to build the gradle project on your local machine so that you recieve all of the necessary libraries (such as JavaFX) to run our game.

Backend files: The backend of our Imperial Game is located in the "src/main/java/Group12/gamelogic" folder which contains all of the classes representing the logic of the game.
Frontend files: The GUI components are located in the "src/main/java/Group12/gui" folder and, aside from the Launcher and Controller classes, contain all necessary information relating to our GUI
All other relevant class files for features such as our agents are their decisionmaking classes are located in appropriately named folders.


How to run and play our Imperial game (FOR HUMANS VS HUMANS):

1. Navigate to the folder "src/main/java/Group12/launchers/" and simply run the ImperialApplicationGUI.java file
2. You will then be prompted after selecting "Start Game" and then you will be asked to enter the amount of players. Before entering your name you
must choose that you are a "Human Player", otherwise the name label will be reset.
 - Side note: If you want any idea of what's going on, you will need to study the imperial rules and learn how the game is played (we offer no tutorial).
   You can find the full documented rulebook here: https://www.fgbradleys.com/rules/rules4/Imperial%20-%20Rules.pdf
   
3. After selecting "Start", you will be greeted with the world map and above you can read which player's turn is currently active. Along the border 
you will see information about the game such as the tax chart (right), the countries and their treasuries (left), the scoring track (bottom), and the 
current player and phase in this turn (top).
 - Clicking and dragging will pan across the map, and zooming via the mousepad will also zoom. 
4. To make a decision, you must first choose a space on the Rondel by selecting the "Rondel" button, then you may drag the corresponding player
piece to the move you wish to make.
    - For example, if you move to "Factory" then return to the map screen you may click on the province you wish to build a factory, and a square should appear representing your new factory.
5. The game should progress with ease through each choice, and if an action does not end a turn in any case, pressing the "Finish Turn" button at the top of the screen should continue the game.
6. If a player won the scores will be printed in the console.

How to run and play our Imperial game with agents:

NOTICE: There are two modes implemented for testing agents, if you wish to test a game with only agents, you must choose "Simulation" and review each
of their turns by pressing the "Next tick" button (top). (a "tick" represents a turn in our implementation, a turn includes a rondelchoice + whether investor was passed).

1. Playing against an agent requires you to perform the steps as mentioned in the Human vs Human game, but simply choosing one of our agents as one of the players.
2. After making a decision as the human player, the agent should immediately respond and you may have to go back to the game board to see what happend.
3. That's all, have fun!

We hope that this readme is informative, and that you have no issue running the game!
