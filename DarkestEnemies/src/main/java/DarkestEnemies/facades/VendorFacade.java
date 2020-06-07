/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Potion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Asger
 */
public class VendorFacade {
    
    private static VendorFacade instance;
    private static EntityManagerFactory emf;

    public static VendorFacade getPlayerFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new VendorFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public void getVendor(){
        EntityManager em = getEntityManager();
       
            
    }
    
}
