/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.IF;

/**
 *
 * @author William
 */
public interface ItemI<T> {
    public T getItem();
    public String getName();
    public String getDescription();
    public int getValue();
}
