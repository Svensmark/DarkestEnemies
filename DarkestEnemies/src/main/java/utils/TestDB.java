/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import DarkestEnemies.Entity.SingleUseItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author APC
 */
public class TestDB {

    public static void main(String[] args) {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
        EntityManager em = emf.createEntityManager();
        
        SingleUseItem s = new SingleUseItem("HP potion", "Heals", 100);
        
        em.getTransaction().begin();
        em.persist(s);
        em.getTransaction().commit();
        
    }

}
