/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.IF;

/**
 *
 * @author emilt
 */
public interface DECharacter {
    //Get methods
    public enum ENUMTYPE{PLAYER, NPC};
    public ENUMTYPE getType();
    public String getCharacterName();
    public int getHealth();
    public int getMana();
    public int getAttackDmg();
    
    
}
