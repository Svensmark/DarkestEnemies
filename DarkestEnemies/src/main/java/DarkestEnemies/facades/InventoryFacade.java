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
        List<Long> potionIds = new ArrayList<Long>();
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
    
    public Inventory getInventory(DECharacter character, Long id){
        EntityManager em = getEntityManager();
        Inventory inventory = em.find(Inventory.class, id);
        return inventory;
    }

    public void addToInventory(DECharacter character, Inventory inventory) {
        EntityManager em = getEntityManager();
        Inventory inv = em.find(Inventory.class, character.getInventory().getId());
        for(Long longs : inventory.getPotionIds()){
            inv.getPotionIds().add(longs);
        }
        try {
            em.getTransaction().begin();
            em.merge(inv);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }
    
    public void removeFromInventory(DECharacter character, int index){
        EntityManager em = getEntityManager();
        Inventory inv = em.find(Inventory.class, character.getInventory().getId());
        inv.getPotionIds().remove(index);
        character.setInventory(inv);
//        List<Long> inventory = character.getInventory().getPotionIds();
//        List<Long> newInventory = new ArrayList();
//        for(Long longs : inventory){
//            newInventory.add(longs);
//        }
//        newInventory.remove(index);
//        inventory = newInventory;
//        Inventory inv = new Inventory(inventory);
//        character.setInventory(inv);
        try{
            em.getTransaction().begin();
            em.merge(inv);
            em.merge(character);
            em.getTransaction().commit();
        }finally{
            em.close();;
        }
    }

}
