/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.IF;

import DarkestEnemies.Entity.HealthPotion;

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
    public int getLevel();
    public int getCurrentExp();
    
    public HealthPotion getHealthpotion();
    public void setCharacterName(String name);
    public void setHealth(int health);
    public void setMana(int mana);
    public void setAttackDmg(int atk);
    public void setLevel(int level);
    public void setCurrentExp(int currentExp);
    public void setHealthpotion(HealthPotion healthpotion);
    
    
    
  
    
    
}
