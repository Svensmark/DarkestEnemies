/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;


/**
 *
 * @author emilt
 */
public class DarkestEnemiesGameServer {

    public static void main(String[] args) {
        int port = 3737;
        GameServerMultithread server = new GameServerMultithread(new DarkestEnemiesGameMultithread(), port);
        server.run();
    }
}
