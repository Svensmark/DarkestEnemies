/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import DarkestEnemies.IF.ItemI;
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
public class Trinket implements Serializable, ItemI {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name, info;
    private int healingValue, manaValue, dmgIncreaseValue;

    public Trinket(String name, String info, int healingValue, int manaValue, int dmgIncreaseValue) {
        this.name = name;
        this.info = info;
        this.healingValue = healingValue;
        this.manaValue = manaValue;
        this.dmgIncreaseValue = dmgIncreaseValue;
    }

    public Trinket() {
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
    public String getInfo() {
        return info;
    }

    @Override
    public int getManaValue() {
        return manaValue;
    }

    @Override
    public int getHealingValue() {
        return healingValue;
    }

    @Override
    public int getDmgIncreaseValue() {
        return dmgIncreaseValue;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public void setManaValue(int value) {
        this.manaValue = value;
    }

    @Override
    public void setHealingValue(int value) {
        this.healingValue = value;
    }

    @Override
    public void setDmgIncreaseValue(int value) {
        this.dmgIncreaseValue = value;
    }

}
