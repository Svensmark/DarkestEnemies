/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

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

    @ManyToMany
    private List<Ability> abilities;

    public NPC(String name, int health, int mana, int attackDmg) {
        this.name = name;
        this.health = health;
        this.mana = mana;
        this.attackDmg = attackDmg;
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

//    public String getRole() {
//        return role;
//    }
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
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public List<Ability> getAbilities() {
        return this.abilities;
    }

    public void addAbilities(Ability ability) {
        this.abilities.add(ability);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getLevel() {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public int getCurrentExp() {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public void setLevel(int level) {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public void setCurrentExp(int currentExp) {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public void addHealthpotion(Potion healthpotion) {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public List<Potion> getHealthpotion() {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public int getMaxHealth() {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public int getMaxMana() {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public int getMaxAttackDmg() {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public void setMaxHealth(int health) {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public void setMaxMana(int mana) {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

    @Override
    public void setMaxAttackDmg(int atk) {
        throw new UnsupportedOperationException("This Class does not use this method. Not allowed to be used.");
    }

}
