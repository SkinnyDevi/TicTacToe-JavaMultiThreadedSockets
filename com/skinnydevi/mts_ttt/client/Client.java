package com.skinnydevi.mtws_ttt.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.Socket;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
    final static int PORT = 38472;
    final static String HOST = "localhost";

    public static void main(String[] args) {
        try {
            Socket sk = new Socket(HOST, PORT);
            sendServer(sk);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void sendServer(Socket sk) {
		InputStream is = null;
        OutputStream os = null;
		
        try {
			is = sk.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader inputReader = new BufferedReader(isr);

            os = sk.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter outputWriter = new BufferedWriter(osw);
            
            try (Scanner sc = new Scanner(System.in)) {
				System.out.println("Let's play TicTacToe!");
				System.out.println("I'll start:");
				outputWriter.write("serverMove");
				outputWriter.newLine();
				outputWriter.flush();
				String res = inputReader.readLine().replace("5", "\n");
				System.out.println(res);
				System.out.println("Coordinates start from left corner up with 1,1");

				boolean someoneWon = false;
				while(!someoneWon) {
					System.out.print("Your turn! Choose a coordinate ('X,Y'): ");
					res = sc.nextLine();
					outputWriter.write(res);
					outputWriter.newLine();
					outputWriter.flush();
					res = inputReader.readLine();

					switch(res) {
						case "wrongMove":
							System.out.println("Hey! You chose somewhere chosen or your answer wasn't correctly formatted: 'X,Y'. Choose another!");
							break;
						default:
							System.out.println("\nYour move: \n" + res.replace("5", "\n"));
							if (!(res.indexOf("WINS") != -1 || res.indexOf("DRAW") != -1)) {
								System.out.println("Server move:");
								outputWriter.write("serverMove");
								outputWriter.newLine();
								outputWriter.flush();
								res = inputReader.readLine();
								try {
									System.out.println(res.replace("5", "\n"));
								} catch (Exception err) {
									System.out.println(res);
								}
								if (res.indexOf("WINS") != -1 || res.indexOf("DRAW") != -1) someoneWon = true;
							} else someoneWon = true;
							break;
					}
				}
			}
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(os != null) os.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
