/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.syncbox.SyncBox;
import DarkestEnemies.textio.ITextIO;
import java.util.ArrayList;

/**
 *
 * @author Gamer
 */
public interface ITextGameMultithread {
    
    public void startGame(ITextIO player, ArrayList<SyncBox> syncBoxes);
    
}
