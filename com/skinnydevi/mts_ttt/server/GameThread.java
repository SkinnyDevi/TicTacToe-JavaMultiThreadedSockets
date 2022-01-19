package com.skinnydevi.mtws_ttt.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameThread extends Thread {
    Socket sk;
	TicTacToe game;

    public GameThread(Socket sk){
        this.sk = sk;
		this.game = new TicTacToe();
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;

        try {
            is = sk.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader inputReader = new BufferedReader(isr);
            
            os = sk.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter outputWriter = new BufferedWriter(osw);

			boolean[] winningTest = new boolean[2];
            while(!winningTest[0]) {
				System.out.println("Waiting command...");
				String cmd = inputReader.readLine();

				String winType;
				switch(cmd) {
					case "serverMove":
						game.serverMovement();
						System.out.println("Made servermove and sent gameboard");

						winningTest = game.checkForWinners(true);
						System.err.println(Arrays.toString(winningTest));
						winType = !winningTest[0] && winningTest[1] ? "5IT'S A DRAW!" : "5SERVER WINS!";

						if (!winningTest[0] && !(!winningTest[0] && winningTest[1])) outputWriter.write("5" + game.generateBoard());
						else outputWriter.write("5" + game.generateBoard() + winType);
						break;
					default:
						boolean err = game.gatherUserMove(cmd);

						if (err) {
							outputWriter.write("wrongMove");
							break;
						}

						System.out.println("Made playermove and sent gameboard");

						winningTest = game.checkForWinners(false);
						if (winningTest[0]) {
							outputWriter.write("5" + game.generateBoard() + "5PLAYER WINS!");
							System.out.println("Player Win - Closing Connection");
						} else {
							outputWriter.write("5" + game.generateBoard());
						}
						break;
				}

				if (!winningTest[0] && winningTest[1]) {
					winningTest[0] = true;
					System.out.println("Draw - Closing Connection");
					outputWriter.newLine();
                	outputWriter.flush();
					continue;
				}
				
                outputWriter.newLine();
                outputWriter.flush();
            }

        } catch (IOException ex) {
            Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
