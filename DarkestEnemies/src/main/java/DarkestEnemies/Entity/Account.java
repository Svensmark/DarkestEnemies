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

/**
 *
 * @author Gamer
 */
@Entity
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private Player character;
    
    public Account() {
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    
    public Long getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    
    public DECharacter getCharacter() {
        return character;
    }

    public void setCharacter(Player character) {
        this.character = character;
    }


    
    
}
