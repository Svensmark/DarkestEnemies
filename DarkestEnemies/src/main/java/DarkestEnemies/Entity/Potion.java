/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import DarkestEnemies.IF.DECharacter;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 *
 * @author Asger
 */
@Entity
public class Potion implements DarkestEnemies.IF.ItemI, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String info;
    private int healingValue;
    private int manaValue;
    private int dmgIncreaseValue;

    @ManyToMany
    private List<Player> player;

    public Potion(String name, String info, int healingValue, int manaValue, int dmgIncreaseValue) {
        this.name = name;
        this.info = info;
        this.healingValue = healingValue;
        this.manaValue = manaValue;
        this.dmgIncreaseValue = dmgIncreaseValue;
    }
    
    

//    public Potion(String name, int healingValue, int manaValue) {
//        this.name = name;
//        if (healingValue > 0 && manaValue > 0) {
//            this.info = "Restores " + healingValue + " health and " + manaValue + " mana.";
//        } else if (healingValue > 0 && manaValue <= 0) {
//            this.info = "Restores " + healingValue + " health.";
//        } else if(healingValue <= 0 && manaValue > 0) {
//            this.info = "Restores " + manaValue + " mana.";
//        }
//
//    }
//    
//     public Potion(String name, int dmgIncreaseValue) {
//        this.name = name;
//        this.info = "increrases your attack damage stat by " + dmgIncreaseValue + " for this encounter.";
//        this.dmgIncreaseValue = dmgIncreaseValue;
//    }

    public Potion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int getHealingValue() {
        return healingValue;
    }

    @Override
    public void setHealingValue(int healingValue) {
        this.healingValue = healingValue;
    }

    @Override
    public int getManaValue() {
        return manaValue;
    }

    @Override
    public void setManaValue(int manaValue) {
        this.manaValue = manaValue;
    }

    @Override
    public int getDmgIncreaseValue() {
        return dmgIncreaseValue;
    }

    @Override
    public void setDmgIncreaseValue(int dmgIncreaseValue) {
        this.dmgIncreaseValue = dmgIncreaseValue;
    }

}
