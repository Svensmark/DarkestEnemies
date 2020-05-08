/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.IF;

/**
 *
 * @author Asger
 */
public interface ItemI {
    public String getName();
    public String getInfo();
    public int getValue();
    public String use(int value);
    public void setName(String name);
    public void setInfo(String info);
    public void setValue(int value);
    
}
