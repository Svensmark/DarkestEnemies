/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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

    //Mandatory stats
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int attackDmg;
    private int maxAttackDmg;
    private int level;
    private int currentExp;
    private int neededExp;

    //Inventory
    @OneToOne(cascade = CascadeType.PERSIST)
    private Inventory inventory;
    private int gold;

    //Abilities
    @ManyToMany
    private List<Ability> abilities;

    public Player(String name, int health, int maxHealth, int mana, int maxMana, int attackDmg, int maxAttackDmg, int level, List<Ability> abilities) {
        this.name = name;
        this.health = health;
        this.maxHealth = maxHealth;
        this.mana = mana;
        this.maxMana = maxMana;
        this.attackDmg = attackDmg;
        this.maxAttackDmg = maxAttackDmg;
        this.level = level;
        this.abilities = abilities;
    }

    public Player(String name) {
        this.name = name;
        this.health = 100;
        this.maxHealth = 100;
        this.mana = 0;
        this.maxMana = 0;
        this.attackDmg = 2;
        this.maxAttackDmg = 2;
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

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    @Override
    public int getMaxAttackDmg() {
        return maxAttackDmg;
    }

    @Override
    public void setMaxAttackDmg(int maxAttackDmg) {
        this.maxAttackDmg = maxAttackDmg;
    }

    @Override
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
            this.neededExp = (int) Math.pow(0.8, this.level) * 1000;
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getGold() {
        return this.gold;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    public void removeGold(int amount) {
        this.gold -= amount;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    @Override
    public List<Ability> getAbilities() {
        return abilities;
    }

}
