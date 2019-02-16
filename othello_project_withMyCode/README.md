# Othello Project

## Getting Started

1. Test the code by running the main method in OthelloGUI.java. This will play an othello game where both players are RandomPlayer.java.
2. Change your division name in Player.java (currently `Tonkotsu Ramen`).

## Game rules

- A player **has 10 second per move**. If the time exeeds, you will be forced to make a random move. Please make sure that your bot do not take too long.
- Depending on the efficiency of your code, your bot might be able to think ahead for several time steps under 10 seconds. However, you need to **limit the depth of the search to 20 steps into the future**.
- The evaluation function must not run additional minimax search into the future move.

## Making a player

- Your player code will inherit the abstract Player in Player.java. This abstract class requires you to implement `move(...)` function.
- You can see RandomPlayer.java as an example. It will sleep for 5 seconds before making a random (but valid) move.
- To have your bot play the game, you just need to change `player` in Black.java or White.java.
