/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.exceptions.AbilityNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import utils.EMF_Creator;

/**
 *
 * @author Gamer
 */
public class AbilityFacade {
    
    private static AbilityFacade instance;
    private static EntityManagerFactory emf;
    
    
    public static AbilityFacade getAbilityFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AbilityFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    
    public Ability persistAbility(Ability ability) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ability);
            em.getTransaction().commit();
            return ability;
        } finally {
            em.close();
        }
    }
    
    public Ability getAbilityByName(String name) throws AbilityNotFoundException{
        Query query = getEntityManager().createQuery("SELECT ability FROM Ability ability WHERE ability.name = :name", Ability.class);
        List<Ability> ability = query.setParameter("name", name).getResultList();     
        
        //The ability was not found
        if (ability.isEmpty()) {
            throw new AbilityNotFoundException("Something went wrong, no ability found with that name");
        }
        
        return ability.get(0);
    }
    
    
}
