/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.HealthPotion;
import DarkestEnemies.IF.DECharacter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Asger
 */
public class InventoryFacade {

    private static InventoryFacade instance;
    private static EntityManagerFactory emf;

    private InventoryFacade() {
    }

    public static InventoryFacade getInventoryFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new InventoryFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public void addHealthPotion(DECharacter character, String name, int value){
        EntityManager em = getEntityManager();
        character.setHealthpotion(new HealthPotion(name, value));
        try{
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }
    
    public void useHealthPotion(DECharacter character){
        EntityManager em = getEntityManager();
        character.getHealthpotion().use(character);
        try{
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
