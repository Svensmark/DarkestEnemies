/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.run;

import DarkestEnemies.textgame.TextGameServer;

/**
 *
 * @author emilt
 */
public class DarkestEnemiesGameServer {
          
    public static void main(String[] args) {
        TextGameServer tgs = new TextGameServer(9999);
        tgs.run();
    }
}
