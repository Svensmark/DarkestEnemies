/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.IF;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Potion;
import DarkestEnemies.Entity.Inventory;
import java.util.List;

/**
 *
 * @author emilt
 */
public interface DECharacter {

    public String getName();
    //Get methods
    public enum ENUMTYPE{PLAYER, NPC};
    public ENUMTYPE getType();
    public Long getId();
    public String getCharacterName();
    public int getHealth();
    public int getMaxHealth();
    public int getMana();
    public int getMaxMana();
    public int getAttackDmg();
    public int getMaxAttackDmg();
    public int getLevel();
    public int getCurrentExp();
    
    public Inventory getInventory();
//    public List<Potion> getHealthpotion();
    public List<Ability> getAbilities();
    
    public void setCharacterName(String name);
    public void setHealth(int health);
    public void setMaxHealth(int health);
    public void setMana(int mana);
    public void setMaxMana(int mana);
    public void setAttackDmg(int atk);
    public void setMaxAttackDmg(int atk);
    public void setLevel(int level);
    public void setCurrentExp(int currentExp);
//    public void addHealthpotion(Potion healthpotion);
    
    public void setInventory(Inventory inventory);
    
    
  
    
    
}
