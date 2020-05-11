/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.HealthPotion;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.ItemNotFoundException;
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

    public void addHealthPotion(HealthPotion healthpotion) {
        EntityManager em = getEntityManager();
        HealthPotion hp = new HealthPotion(healthpotion.getName(), healthpotion.getValue());
        try {
            em.getTransaction().begin();
            em.persist(hp);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void addPotionToPlayer(Long id, HealthPotion hp) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Player player = em.find(Player.class, id);
            HealthPotion healthpotion = em.find(HealthPotion.class, hp.getId());
            player.addHealthpotion(healthpotion);

            em.merge(player);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public HealthPotion getHealthPotionByID(Long id) throws ItemNotFoundException {
        EntityManager em = getEntityManager();
        HealthPotion hp = em.find(HealthPotion.class, id);
        if(hp == null){
            throw new ItemNotFoundException("Item doesn't seem to exist.");
        }
        
        return hp;
    }

    public void useHealthPotion(DECharacter character, int index) {
        EntityManager em = getEntityManager();
        character.getHealthpotion().get(index).use(character);
        try {
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
