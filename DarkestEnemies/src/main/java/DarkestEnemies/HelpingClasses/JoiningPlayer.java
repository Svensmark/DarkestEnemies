/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.HelpingClasses;

import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.textio.ITextIO;

/**
 *
 * @author Gamer
 */
public class JoiningPlayer {
    
    private ITextIO playerIO;
    private DECharacter playerCharacter;

    public JoiningPlayer(ITextIO playerIO, DECharacter playerCharacter) {
        this.playerIO = playerIO;
        this.playerCharacter = playerCharacter;
    }

    public ITextIO getPlayerIO() {
        return playerIO;
    }

    public DECharacter getPlayerCharacter() {
        return playerCharacter;
    }
    
    
    
}
