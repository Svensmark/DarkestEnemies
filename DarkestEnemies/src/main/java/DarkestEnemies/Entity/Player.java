/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

/**
 *
 * @author William
 */
public class Player implements DarkestEnemies.IF.Character {
    
    private String name;
    private int health;
    private int mana;
    private int attackDmg;

    public Player(String name, int health, int mana, int attackDmg) {
        this.name = name;
        this.health = health;
        this.mana = mana;
        this.attackDmg = attackDmg;
    }
    
    
    @Override
    public ENUMTYPE getType() {
        return ENUMTYPE.PLAYER;
    }

    @Override
    public String getCharacterName() {
        return name;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public int getAttackDmg() {
        return attackDmg;
    }

    public void setCharacterName(String name) {
        this.name = name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setAttackDmg(int attackDmg) {
        this.attackDmg = attackDmg;
    }
    
    

}
