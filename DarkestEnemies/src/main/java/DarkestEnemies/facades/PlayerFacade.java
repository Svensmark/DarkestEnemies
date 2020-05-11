/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.exceptions.CharacterNotFoundException;
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
    
    public void persistPlayer(Player player) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(player);
            em.getTransaction().commit();
        } finally {
            em.close();            
        }
    }
    
    public Player getPlayerByID(Long id) throws CharacterNotFoundException{
        EntityManager em = getEntityManager();
        Player player = em.find(Player.class, id);
        if(player == null){
            throw new CharacterNotFoundException("Character with that ID was not found.");
        }
        return player;
    }
    
    public void addAbilityToPlayer(Long playerID, Ability ability) {
        EntityManager em = getEntityManager();
         
        try {
            em.getTransaction().begin();    
            Player foundPlayer = em.find(Player.class, playerID);
            Ability foundAbility = em.find(Ability.class, ability.getId());
            
            foundPlayer.addAbility(foundAbility);
            
            em.merge(foundPlayer);
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
