/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Inventory;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.IF.DECharacter;
import java.util.ArrayList;
import java.util.List;
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
    
    public void setupInventory(Player character){
        EntityManager em = getEntityManager();
        List<Long> potionIds = new ArrayList();
        Inventory inv = new Inventory(potionIds);
        em.find(Player.class, character.getId());
        character.setInventory(inv);
        try{
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }

    public void addToInventory(DECharacter character, Inventory inventory) {
        EntityManager em = getEntityManager();
        character.setInventory(inventory);
        try {
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

}
