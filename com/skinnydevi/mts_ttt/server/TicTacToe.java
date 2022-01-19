package com.skinnydevi.mts_ttt.server;

import java.util.Arrays;
import java.util.Random;

public class TicTacToe {
	private int[][] valueBoard = {
			{ -1, -1, -1 },
			{ -1, -1, -1 },
			{ -1, -1, -1 }
	}; // server (0) = X, player (1) = O

	public TicTacToe() {}

	public boolean gatherUserMove(String move) {
		if (move.length() > 3) return true;
		String[] values = move.replace(" ", "").split(",");

		int x, y;

		try {
			x = Integer.parseInt(values[0]);
			y = Integer.parseInt(values[1]);
		} catch (Exception err) {
			return true;
		}

		if ((x < 1 || x > 3) || (y < 1 || y > 3)) return true;

		if (valueBoard[x - 1][y - 1] == -1) {
			setNewPlacement(x, y, false);
			return false;
		} else {
			return true;
		}
	}

	public void serverMovement() {
		boolean notOccupied = false;

		while (!notOccupied) {
			int x = randomVal(1, 3);
			int y = randomVal(1, 3);

			if (valueBoard[x - 1][y - 1] == -1) {
				setNewPlacement(x, y, true);
				notOccupied = true;
			}
		}
	}

	public String generateBoard() {
		String viewBoard = "";

		for (int i = 0; i < valueBoard.length; i++) {
			for (int t = 0; t < valueBoard[i].length; t++) {
				if (valueBoard[i][t] != -1) viewBoard += valueBoard[i][t] == 0 ? "X" : "O";
				else viewBoard += "?";

				if (t != 2) viewBoard += " | ";
			}
			viewBoard += "5";
		}

		return viewBoard;
	}

	public boolean[] checkForWinners(boolean isServerMovement) {
		int[][][] winList = { // Each entry has winning coordinates
			{{1,1}, {1,2}, {1,3}},
			{{2,1}, {2,2}, {2,3}},
			{{3,1}, {3,2}, {3,3}},
			{{1,1}, {2,2}, {3,3}},
			{{1,3}, {2,2}, {3,1}},
			{{1,1}, {2,1}, {3,1}},
			{{1,2}, {2,2}, {3,2}},
			{{1,3}, {2,3}, {3,3}}
		};

		int[][] playerMovements = getMovements(false);
		int[][] serverMovements = getMovements(true);

		boolean[] hasWinningCombination = {false, false}; // [someone won? or draw?, who won? (server = false, player = true)]
		for (int a = 0; a < winList.length; a++) {
			int[][] playerType = isServerMovement ? serverMovements : playerMovements; // Choose which type of player to check for win
			
			if (!hasWinningCombination[0]) {
				boolean[] comboWin = {false, false, false, false, false}; // Min. 3 trues to represent win

				for (int winState = 0; winState < winList[a].length; winState++) { // Get First Win Coords
					for (int movement = 0; movement < playerType.length; movement++) { // Get First Player Coords
						if (comboWin[movement]) continue; // If this combo is already true, skip it
						if (Arrays.equals(winList[a][winState], playerType[movement])) comboWin[movement] = true;
					}
				}
				
				int winCount = 0;
				for (int hasWon = 0; hasWon < comboWin.length; hasWon++) {
					if (winCount == 3) {
						hasWinningCombination[0] = true;
						hasWinningCombination[1] = Arrays.deepEquals(playerType, serverMovements) ? false : true;
						break;
					}
					if (comboWin[hasWon]) winCount++;

				}
			} else {
				break;
			}
		}

		if (!hasWinningCombination[0]) {
			boolean allFilled = true;

			for (int x = 0; x < valueBoard.length; x++) {
				for (int y = 0; y < valueBoard[x].length; y++) {
					if (valueBoard[x][y] == -1) allFilled = false;
 				} 
			}

			if (allFilled) hasWinningCombination[1] = true; // returns {false, true}, indicating a draw
		}

		return hasWinningCombination;
	}

	private void setNewPlacement(int x, int y, boolean isServer) {
		int value = -1;

		if (isServer) value = 0;
		else value = 1;

		valueBoard[x - 1][y - 1] = value;
	}

	private int randomVal(int min, int max) {
		return new Random().nextInt(max - min + 1) + min;
	}

	private int[][] getMovements(boolean isServer) {
		int[][] movements = new int[5][2];

		int playerType = -1;
		if (isServer) playerType = 0;
		else playerType = 1;

		int c = 0;
		for (int x = 0; x < valueBoard.length; x++) {
			for (int y = 0; y < valueBoard[x].length; y++) {
				if (valueBoard[x][y] == playerType) {
					movements[c] = new int[] {x+1, y+1};
					c++;
				}
			}
		}

		return movements;
	}
}
