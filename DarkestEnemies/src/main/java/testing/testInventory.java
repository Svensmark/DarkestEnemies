/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.HealthPotion;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.exceptions.CharacterNotFoundException;
import DarkestEnemies.exceptions.ItemNotFoundException;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.InventoryFacade;
import DarkestEnemies.facades.PlayerFacade;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

/**
 *
 * @author Asger
 */
public class testInventory {

    public static void main(String[] args) throws ItemNotFoundException, CharacterNotFoundException {
        EntityManagerFactory _emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
        
        //Setups an account and a character to that account:
        PlayerFacade pfc = PlayerFacade.getPlayerFacade(_emf);
        AccountFacade acF = AccountFacade.getAccountFacade(_emf);
        InventoryFacade ifc = InventoryFacade.getInventoryFacade(_emf);
        Player player1 = new Player("Emil");
        Account acc1 = acF.createAccount("Svense", "test");
        acF.addCharacterToAccount(acc1, player1);

        //Setups an account and a character to that account:
        Player player2 = new Player("Asger");
        Account acc2 = acF.createAccount("Asger", "test");
        acF.addCharacterToAccount(acc2, player2);
        
        HealthPotion hp1 = new HealthPotion("smaller hp", 10);
        HealthPotion hp2 = new HealthPotion("small hp", 20);
        

        ifc.addHealthPotion(hp1);
        ifc.addHealthPotion(hp2);



//        HealthPotion hp1 = ifc.getHealthPotionByID(1L);
//        HealthPotion hp2 = ifc.getHealthPotionByID(2L);
//        Player player1 = pfc.getPlayerByID(1L);
//        Player player2 = pfc.getPlayerByID(2L);
//        
//        ifc.addPotionToPlayer(player1.getId(), hp1);
//        ifc.addPotionToPlayer(player1.getId(), hp2);
//        ifc.addPotionToPlayer(player2.getId(), hp1);
//        ifc.addPotionToPlayer(player2.getId(), hp2);

    }
}
