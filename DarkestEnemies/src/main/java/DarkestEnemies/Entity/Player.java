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

    public Player(String name, int health, int mana, int attackDmg) {
        this.name = name;
        this.health = health;
        this.mana = mana;
        this.attackDmg = attackDmg;
    }

    public Player(String name) {
        this.name = name;
        this.health = 10;
        this.mana = 10;
        this.attackDmg = 2;
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
    

}
