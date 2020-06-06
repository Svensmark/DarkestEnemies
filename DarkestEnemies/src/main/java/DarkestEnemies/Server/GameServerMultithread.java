/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.syncbox.SyncBox;
import DarkestEnemies.textio.ITextIO;
import DarkestEnemies.HelpingClasses.HostingPlayer;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Gamer
 */
public class GameServerMultithread implements Runnable {

    private final ITextGameMultithread game;
    private final int port;
    private ServerSocket serverSocket;

    public GameServerMultithread(ITextGameMultithread game, int port) {
        this.game = game;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            //Gets the IP address from an environment virable named My_IP
            String adr = System.getenv("My_IP");
            //Creates a new serversocket
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on " + adr + ":" + port);

            //All playerIO hosting a multiplayer game as a list in a syncbox
            SyncBox<ArrayList<HostingPlayer>> hostingPlayersList = new SyncBox();
            hostingPlayersList.put(new ArrayList());
            
            SyncBox<HashMap> allSyncBoxes = new SyncBox();
            HashMap allSyncBoxesHM = new HashMap();
            allSyncBoxesHM.put("hostingPlayersList", hostingPlayersList);
            allSyncBoxes.put(allSyncBoxesHM);
            
            //Main server loop
            boolean serverUp = true;
            while (serverUp) {
                try {
                    //Throws a SocketException when closed();
                    Socket socket = serverSocket.accept();
                    GameServerMultithread.TextIOServerSocket textSocket = new GameServerMultithread.TextIOServerSocket(socket);

                    //Not sure what this line does
                    new Thread(textSocket).start();

                    //Starts a new thread for the player and starts the game in that thread with the player
                    Thread newThread = new Thread() {
                        @Override
                        public void run() {
                            game.startGame(textSocket, allSyncBoxes);
                        }
                    };
                    newThread.start(); //Starts the thread here

                } catch (SocketException e) {
                    // Nothing to do, this is actually not an error, 
                    // but the only way we can break out of the accept method.
                }
            }

        } catch (IOException e) {
            System.out.println("Server crashed!");
            throw new RuntimeException(e);
        }
        System.out.println("Server stopped!");
    }

    private class TextIOServerSocket implements ITextIO, Runnable {

        private final Socket socket;
        private final SyncBox<String> cmdBox;
        private final SyncBox<String> putBox;
        private final SyncBox<String> getBox;

        public TextIOServerSocket(Socket socket) {
            this.socket = socket;
            cmdBox = new SyncBox<>();
            putBox = new SyncBox<>();
            getBox = new SyncBox<>();
        }

        @Override
        public void put(String s) {
            cmdBox.put("put");
            putBox.put(s);
        }

        @Override
        public void clear() {
            cmdBox.put("clear");
        }

        @Override
        public String get() {
            cmdBox.put("get");
            return getBox.get();
        }

        @Override
        public void close() throws IOException {
            cmdBox.put("close");
        }

        @Override
        public void run() {
            try {
                DataInput in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutput out = new DataOutputStream(socket.getOutputStream());
                String cmd = cmdBox.get();
                while (!"close".equalsIgnoreCase(cmd)) {
                    if ("put".equalsIgnoreCase(cmd)) {
                        out.writeUTF("put");
                        out.writeUTF(putBox.get());
                    } else if ("clear".equalsIgnoreCase(cmd)) {
                        out.writeUTF("clear");
                    } else if ("get".equalsIgnoreCase(cmd)) {
                        out.writeUTF("get");
                        getBox.put(in.readUTF());
                    } else {
                        throw new RuntimeException("Unknown protocol command: " + cmd);
                    }
                    cmd = cmdBox.get();
                }
                out.writeUTF("close");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    //Nothing we can do here...
                }
            }
        }

    }
}
