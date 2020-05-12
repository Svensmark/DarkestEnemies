/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.Potion;
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
//        Player player1 = new Player("Emil");
//        Account acc1 = acF.createAccount("Svense", "test");
//        acF.addCharacterToAccount(acc1, player1);
//
//        //Setups an account and a character to that account:
//        Player player2 = new Player("Asger");
//        Account acc2 = acF.createAccount("Asger", "test");
//        acF.addCharacterToAccount(acc2, player2);
//
//        Potion hp1 = new Potion("Slight Healing Potion", "Restores 10 health", 10, 0, 0);
//        Potion hp2 = new Potion("Small Healing Potion", "Restores 20 health", 20, 0, 0);
//        Potion hp3 = new Potion("Medium Healing Potion", "Restores 30 health", 30, 0, 0);
//        Potion hp4 = new Potion("Large Healing Potion", "Restores 40 health", 40, 0, 0);
//
//        Potion mp1 = new Potion("Slight Mana Potion", "Restores 10 mana", 0, 10, 0);
//        Potion mp2 = new Potion("Small Mana Potion", "Restores 20 mana", 0, 20, 0);
//        Potion mp3 = new Potion("Medium Mana Potion", "Restores 30 mana", 0, 30, 0);
//        Potion mp4 = new Potion("Large Mana Potion", "Restores 40 mana", 0, 40, 0);
//
//        Potion ap1 = new Potion("Essence of the Monkey", "Increases your dmg by 2 for this encounter.", 0, 0, 2);
//        Potion ap2 = new Potion("Essence of the Tiger", "Increases your dmg by 4 for this encounter.", 0, 0, 4);
//        Potion ap3 = new Potion("Essence of the Gorilla", "Increases your dmg by 6 for this encounter.", 0, 0, 6);
//        Potion ap4 = new Potion("Essence of the Titans", "Increases your dmg by 10 for this encounter.", 0, 0, 10);
////        
//
//        ifc.addPotion(hp1);
//        ifc.addPotion(hp2);
//        ifc.addPotion(hp3);
//        ifc.addPotion(hp4);
//
//        ifc.addPotion(mp1);
//        ifc.addPotion(mp2);
//        ifc.addPotion(mp3);
//        ifc.addPotion(mp4);
//        
//        ifc.addPotion(ap1);
//        ifc.addPotion(ap2);
//        ifc.addPotion(ap3);
//        ifc.addPotion(ap4);
        Potion p1 = ifc.getPotionByID(1L);
        Potion p2 = ifc.getPotionByID(5L);
        Potion p3 = ifc.getPotionByID(9L);
        Player player1 = pfc.getPlayerByID(1L);
        Player player2 = pfc.getPlayerByID(2L);
        
        ifc.addPotionToPlayer(player1.getId(), p1);
        ifc.addPotionToPlayer(player1.getId(), p2);
        ifc.addPotionToPlayer(player1.getId(), p3);
        ifc.addPotionToPlayer(player2.getId(), p1);
        ifc.addPotionToPlayer(player2.getId(), p2);
        ifc.addPotionToPlayer(player2.getId(), p3);
    }
}
