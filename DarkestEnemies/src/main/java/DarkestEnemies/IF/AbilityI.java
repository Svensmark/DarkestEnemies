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
public interface AbilityI {
    
    public int getDamage();
    public int getHealing();
    
    public int getAmountOfTargets();
    public int getRank();
    
    public String getName();
    public String getDescription();
    
}
