/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.IF;

/**
 *
 * @author emilt
 */
public interface Account {
    
    public void login();
    public DECharacter getCharacter();
    public void createCharacter(String name);
    
}
