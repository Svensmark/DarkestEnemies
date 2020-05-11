/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import DarkestEnemies.IF.DECharacter;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Asger
 */
@Entity
public class HealthPotion implements DarkestEnemies.IF.ItemI, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String info;
    private int value; 
    @ManyToOne
    private Player player;

    public HealthPotion(String name, int value) {
        this.name = name;
        this.info = "Potion that heals you for " + value;
        this.value = value;
    }

    public HealthPotion() {
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
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void use(DECharacter player) {
        player.setHealth(player.getHealth() + value);
        System.out.println("Healed for " + value);
        System.out.println("You now have: " + player.getHealth() + "hp.");
    }

   
    
}
