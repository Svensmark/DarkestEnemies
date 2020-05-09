/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import DarkestEnemies.IF.AbilityI;
import DarkestEnemies.IF.DECharacter;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Gamer
 */
@Entity
public class Ability implements Serializable, AbilityI {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int damage;
    private int healing;
    
    private int amountOfTargets;
    private int rank;
    
    private String name;
    private String description;

    
    public Ability(int damage, int healing, int amountOfTargets, String name, String description) {
        this.damage = damage;
        this.healing = healing;
        this.name = name;
        this.description = description;
        this.amountOfTargets = amountOfTargets;
    }

    public Ability() {
    }
    

    public Long getId() {
        return this.id;
    }

    @Override
    public int getDamage() {
        return this.damage;
    }

    @Override
    public int getHealing() {
        return this.healing;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getAmountOfTargets() {
        return this.amountOfTargets;
    }

    @Override
    public int getRank() {
        return this.rank;
    }

    
    
    
}
