/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author William
 */
@Entity
public class Player implements DarkestEnemies.IF.DECharacter, Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private int health;
    private int mana;
    private int attackDmg;
    private int level;
    private int currentExp;
    private int neededExp;
    private HealthPotion healthpotion;

    public Player(String name, int health, int mana, int attackDmg, int level) {
        this.name = name;
        this.health = health;
        this.mana = mana;
        this.attackDmg = attackDmg;
        this.level = level;
    }

    public Player(String name) {
        this.name = name;
        this.health = 100;
        this.mana = 0;
        this.attackDmg = 2;
        this.level = 1;
    }    
    
    public Player() {
        
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

    @Override
    public void setCharacterName(String name) {
        this.name = name;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public void setMana(int mana) {
        this.mana = mana;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getCurrentExp() {
        return currentExp;
    }

    @Override
    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
    }

    public int getNeededExp() {
        return neededExp;
    }

    public void setNeededExp(int neededExp) {
        this.neededExp = neededExp;
    }

    @Override
    public void setAttackDmg(int attackDmg) {
        this.attackDmg = attackDmg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }    
    
    public void checkExp() {
        if (this.currentExp >= this.neededExp) {
            this.level = this.level + 1;
            this.currentExp = this.currentExp - this.neededExp;
            this.neededExp = (int) Math.pow(0.8, this.level)*1000;
        }
    }

    public HealthPotion getHealthpotion() {
        return healthpotion;
    }

    public void setHealthpotion(HealthPotion healthpotion) {
        this.healthpotion = healthpotion;
    }
    
    
    

}
