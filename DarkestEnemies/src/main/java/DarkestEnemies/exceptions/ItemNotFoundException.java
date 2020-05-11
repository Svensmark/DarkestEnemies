/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.exceptions;

/**
 *
 * @author Asger
 */
public class ItemNotFoundException extends Exception {
    
    public ItemNotFoundException(String ErrorMessage){
        super(ErrorMessage);
    }
}
