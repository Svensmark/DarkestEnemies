/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.IF;

import DarkestEnemies.textio.ITextIO;
/**
 *
 * @author emilt
 */
public interface DEGameCtrl {
    
    public void start(DECharacter player, ITextIO textIO);
    
    public void showMenu(DECharacter player, ITextIO textIO);
    public DECharacter findEnemy(DECharacter player, ITextIO textIO);
    public void logOut(DECharacter player, ITextIO textIO);
    
    public void combat(Iterable<DECharacter> Team1Characters, Iterable<DECharacter> Team2Characters);
    
}
