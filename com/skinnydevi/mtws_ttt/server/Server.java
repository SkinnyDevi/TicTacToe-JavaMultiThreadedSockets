package com.skinnydevi.mtws_ttt.server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    public static void main(String[] args) {
        final int PORT = 38472;
    
        try {
            try (ServerSocket sk = new ServerSocket(PORT)) {
				int connectionCount = 0;
				while(true) {
				    Socket socket = sk.accept();
					connectionCount++;
				    System.out.println("Connection stablished: +" + connectionCount);
				    GameThread t = new GameThread(socket);
				    t.start();
				}
			}
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
