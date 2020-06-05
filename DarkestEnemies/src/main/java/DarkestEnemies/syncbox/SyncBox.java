/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.syncbox;

/**
 *
 * @author Tobias Grundtvig
 */
public class SyncBox<E>
{
    private E obj = null;
    
    public synchronized E get()
    {
        while(obj == null)
        {
            try
            {
                wait();
            } catch (InterruptedException ex)
            {
                //Do nothing...
            }
        }
        E res = obj;
        obj = null;
        notifyAll();
        return res;
    }
    
    public synchronized void put(E obj)
    {
        while(this.obj != null)
        {
            try
            {
                wait();
            } catch (InterruptedException ex)
            {
                //Do nothing...
            }
        }
        this.obj = obj;
        notifyAll();
    }
    
    public synchronized E peek() {
        while (this.obj == null) {
            try {
                wait();
            } catch (InterruptedException ex) {
                //Do nothing...
            }
        }
        return this.obj;
    }
}
