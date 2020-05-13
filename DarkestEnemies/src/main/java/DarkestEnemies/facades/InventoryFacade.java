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

    public void setupInventory(Player character) {
        EntityManager em = getEntityManager();
        List<Long> potionIds = new ArrayList<Long>();
        Inventory inv = new Inventory(potionIds);
        em.find(Player.class, character.getId());
        character.setInventory(inv);
        try {
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Inventory getInventory(DECharacter character, Long id) {
        EntityManager em = getEntityManager();
        Inventory inventory = em.find(Inventory.class, id);
        return inventory;
    }

    public void addToInventory(DECharacter character, Inventory newInventory) {
        EntityManager em = getEntityManager();
        Inventory playerInventory = null;

        //If an inventory needs to be setup
        try {
            playerInventory = em.find(Inventory.class, character.getInventory().getId());
        } catch (NullPointerException e) {
            setupInventory((Player) character);
        }
        
        //Adds the reward to the player
        if (newInventory != null) {
            for (Long longs : newInventory.getPotionIds()) {
                playerInventory.getPotionIds().add(longs);
            }
        }

        try {
            em.getTransaction().begin();
            em.merge(playerInventory);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    public void removeFromInventory(DECharacter character, int index) {
        EntityManager em = getEntityManager();
        Inventory inv = em.find(Inventory.class, character.getInventory().getId());
        inv.getPotionIds().remove(index);
        character.setInventory(inv);
        try {
            em.getTransaction().begin();
            em.merge(inv);
            em.merge(character);
            em.getTransaction().commit();
        } finally {
            em.close();;
        }
    }

}
