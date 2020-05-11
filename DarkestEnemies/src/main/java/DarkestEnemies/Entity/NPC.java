/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author William
 */
@Entity
public class NPC implements DarkestEnemies.IF.DECharacter, Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private int health;
    private int mana;
    private int attackDmg;
    private String role;

    
    // Constructor for enemy NPC's
    public NPC(String name, int health, int mana, int attackDmg) {
        this.name = name;
        this.health = health;
        this.mana = mana;
        this.attackDmg = attackDmg;
        this.role = "Enemy";
    }

    // Constructor for friendly NPC's
    public NPC(String name) {
        this.name = name;
        this.role = "Friendly";
    }

    public NPC() {
    }
    
    
    
    @Override
    public ENUMTYPE getType() {
        return ENUMTYPE.NPC;
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

    public String getRole() {
        return role;
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

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int getLevel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCurrentExp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLevel(int level) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCurrentExp(int currentExp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

    @Override
    public void addHealthpotion(HealthPotion healthpotion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<HealthPotion> getHealthpotion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
