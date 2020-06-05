/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Trinket;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.ItemNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author William
 */
public class TrinketFacade {

    private static TrinketFacade instance;
    private static EntityManagerFactory emf;

    public TrinketFacade() {
    }

    public static TrinketFacade getInventoryFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TrinketFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void addTrinket(Trinket trinket) {
        EntityManager em = getEntityManager();

        Trinket trink = new Trinket(trinket.getName(), trinket.getInfo(), trinket.getHealingValue(), trinket.getManaValue(), trinket.getDmgIncreaseValue());

        try {
            em.getTransaction().begin();
            em.persist(trink);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    public Trinket getTrinketById(Long id) throws ItemNotFoundException {
        EntityManager em = getEntityManager();
        Trinket trinket = em.find(Trinket.class, id);

        if (trinket == null) {
            throw new ItemNotFoundException("Trinket doesn't seem to exist.");
        }

        return trinket;
    }

    public void equipTrinket(DECharacter character, Trinket trinket) {
        EntityManager em = getEntityManager();

        // Attack increase
        character.setMaxAttackDmg(character.getMaxAttackDmg() + trinket.getDmgIncreaseValue());
        character.setAttackDmg(character.getAttackDmg() + trinket.getDmgIncreaseValue());

        // Health increase
        character.setMaxHealth(character.getMaxHealth() + trinket.getHealingValue());

        // Mana increase
        character.setMaxMana(character.getMaxMana() + trinket.getManaValue());

        try {
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    public void unequipTrinket(DECharacter character, Trinket trinket) {
        EntityManager em = getEntityManager();

        // Attack decrease
        character.setMaxAttackDmg(character.getMaxAttackDmg() - trinket.getDmgIncreaseValue());
        character.setAttackDmg(character.getAttackDmg() - trinket.getDmgIncreaseValue());

        // Health decrease
        character.setMaxHealth(character.getMaxHealth() - trinket.getHealingValue());

        // Mana decrease
        character.setMaxMana(character.getMaxMana() - trinket.getManaValue());

        try {
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}
