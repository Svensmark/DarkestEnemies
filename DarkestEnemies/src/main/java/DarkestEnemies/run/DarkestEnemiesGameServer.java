/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.run;

import DarkestEnemies.Server.DarkestEnemiesGame;
import DarkestEnemies.Server.GameServer;

/**
 *
 * @author emilt
 */
public class DarkestEnemiesGameServer {

    public static void main(String[] args) {
        int port = 3737;
        GameServer server = new GameServer(new DarkestEnemiesGame(), port);
        server.run();
    }
}
