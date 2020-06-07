/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Potion;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.ItemNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Asger
 */
public class PotionFacade {

    private static PotionFacade instance;
    private static EntityManagerFactory emf;

    private PotionFacade() {
    }

    public static PotionFacade getInventoryFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PotionFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public List<Potion> getAllPotions(){
        return getEntityManager().createQuery("SELECT potions FROM Potion potions", Potion.class).getResultList();
    }

    public void addPotion(Potion potion) {
        EntityManager em = getEntityManager();

        Potion pot = new Potion(potion.getName(), potion.getInfo(), potion.getHealingValue(), potion.getManaValue(), potion.getDmgIncreaseValue(), potion.getBuyValue(), potion.getSellValue());

        try {
            em.getTransaction().begin();
            em.persist(pot);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void addDamagePotion(Potion potion) {
        EntityManager em = getEntityManager();

        Potion pot = new Potion(potion.getName(), potion.getInfo(), potion.getHealingValue(), potion.getManaValue(), potion.getDmgIncreaseValue(), potion.getBuyValue(), potion.getSellValue());

        try {
            em.getTransaction().begin();
            em.persist(pot);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Potion getPotionByID(Long id) throws ItemNotFoundException {
        EntityManager em = getEntityManager();
        Potion hp = em.find(Potion.class, id);
        if (hp == null) {
            throw new ItemNotFoundException("Item doesn't seem to exist.");
        }

        return hp;
    }

    public void usePotion(DECharacter character, Potion potion) {
        EntityManager em = getEntityManager();
        if (potion.getHealingValue() > 0 || potion.getManaValue() > 0) {

            if ((character.getHealth() + potion.getHealingValue()) > character.getMaxHealth()) {
                character.setHealth(character.getMaxHealth());
            } else {
                character.setHealth(character.getHealth() + potion.getHealingValue());
            }

            if ((character.getMana() + potion.getManaValue()) > character.getMaxMana()) {
                character.setMana(character.getMaxMana());
            } else {
                character.setMana(character.getMana() + potion.getManaValue());
            }
        }
        int currentAtk = character.getAttackDmg();
        if (character.getAttackDmg() > character.getMaxAttackDmg()) {
            character.setAttackDmg(character.getMaxAttackDmg());
        }
        try {
            em.getTransaction().begin();
            em.merge(character);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        if (potion.getDmgIncreaseValue() > 0) {
            character.setAttackDmg(character.getAttackDmg() + potion.getDmgIncreaseValue());
            System.out.println(character.getAttackDmg());
        } else {
            character.setAttackDmg(currentAtk);
        }
    }

}
