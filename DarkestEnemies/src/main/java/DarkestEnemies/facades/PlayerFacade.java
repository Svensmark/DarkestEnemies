/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Player;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Gamer
 */
public class PlayerFacade {
    private static PlayerFacade instance;
    private static EntityManagerFactory emf;
    
    
    public static PlayerFacade getPlayerFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PlayerFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    private void updatePlayer(Player player) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(player);
            em.getTransaction().commit();
        } finally {
            em.close();            
        }
    }
    
    public void addAbilityToPlayer(Player player, Ability ability) {
        EntityManager em = getEntityManager();
        player.addAbility(ability);
        try {
            em.getTransaction().begin();            
            em.merge(player);
            em.getTransaction().commit();
        } finally {
            em.close();            
        }
    }
    
    public void addGoldToPlayer(Player player, int amount) {
        player.addGold(amount);
        updatePlayer(player);
    }
    
    public void removeGoldFromPlayer(Player player, int amount) {
        player.removeGold(amount);
        updatePlayer(player);
    }
    
}
