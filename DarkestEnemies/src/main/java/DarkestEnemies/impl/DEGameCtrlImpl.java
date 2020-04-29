/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.impl;

import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.IF.DEGameCtrl;
import DarkestEnemies.textio.ITextIO;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author emilt
 */
public class DEGameCtrlImpl implements DEGameCtrl {

    @Override
    public void start(DECharacter player, ITextIO textIO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showMenu(DECharacter player, ITextIO textIO) {
        //Game is active
        boolean active = true;
        
        while (active) {
            textIO.clear();
            ArrayList<String> choices = new ArrayList<String>(Arrays.asList("Find enemy", "Inventory", "Log out"));
            int index = textIO.select("Test header", choices, "Test footer");
            switch (index) {
                case 1:
                    //Finds an enemy player with findEnemy() and uses combat(player,enemy).
                    //Sets them as lists
                    combat(Arrays.asList(player), Arrays.asList(findEnemy(player, textIO)));
                    break;
                case 2:
                    showInventory(player, textIO);
                    break;
                case 3:
                    logOut(player, textIO);
                    active = false;
                    break;
            }
        }
    }

    @Override
    public DECharacter findEnemy(DECharacter player, ITextIO textIO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void combat(Iterable<DECharacter> Team1Characters, Iterable<DECharacter> Team2Characters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logOut(DECharacter player, ITextIO textIO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showInventory(DECharacter player, ITextIO textIO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
