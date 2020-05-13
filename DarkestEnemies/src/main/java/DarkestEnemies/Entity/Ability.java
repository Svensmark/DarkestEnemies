/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 *
 * @author Gamer
 */
@Entity
public class Ability implements DarkestEnemies.IF.AbilityI, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int damage;
    private int healing;

    private int amountOfTargets;
    private int abilityRank;

    private String name;
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @ManyToMany
    private List<NPC> nPCs;
    @ManyToMany
    private List<Player> player;

    public Ability(int damage, int healing, int amountOfTargets, int abilityRank, String name, String description) {
        this.damage = damage;
        this.healing = healing;
        this.abilityRank = abilityRank;
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
        return this.abilityRank;
    }

}
