/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.Inventory;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.Entity.Potion;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.AbilityNotFoundException;
import DarkestEnemies.exceptions.CharacterNotFoundException;
import DarkestEnemies.exceptions.ItemNotFoundException;
import DarkestEnemies.exceptions.PlayerNotFoundException;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.InventoryFacade;
import DarkestEnemies.facades.PlayerFacade;
import DarkestEnemies.facades.PotionFacade;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

/**
 *
 * @author Asger
 */
public class testInventory {

    public static void main(String[] args) {
        EntityManagerFactory _emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);

        //Setups an account and a character to that account:
        PlayerFacade pfc = PlayerFacade.getPlayerFacade(_emf);
        AccountFacade acF = AccountFacade.getAccountFacade(_emf);
        PotionFacade potfc = PotionFacade.getInventoryFacade(_emf);
        InventoryFacade ifc = InventoryFacade.getInventoryFacade(_emf);
        AbilityFacade afc = AbilityFacade.getAbilityFacade(_emf);

        //Setup Abilities
        afc.setupBasicAbilities();

        SetupPlayers(acF);

        try {
            setupPlayerData(_emf);
        } catch (PlayerNotFoundException ex) {
            Logger.getLogger(testInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AbilityNotFoundException ex) {
            Logger.getLogger(testInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CharacterNotFoundException ex) {
            Logger.getLogger(testInventory.class.getName()).log(Level.SEVERE, null, ex);
        }

        SetupPotions(potfc);

        try {
            setupCharacterPotions(pfc, ifc);
            //testUsePotion(pfc, potfc);
        } catch (CharacterNotFoundException ex) {
            Logger.getLogger(testInventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void testUsePotion(PlayerFacade pfc, PotionFacade potfc) throws CharacterNotFoundException, ItemNotFoundException {
        Player player1 = pfc.getPlayerByID(1L);
        List<Long> inventory = player1.getInventory().getPotionIds();
        for (Long longs : inventory) {
            System.out.println(longs);
        }
        Long selected = inventory.get(0);
            
        Potion pot = potfc.getPotionByID(selected);
        potfc.usePotion(player1, pot);
    }

    private static void SetupPlayers(AccountFacade acF) {
        //Setup first user
        Player player1 = new Player("Emil");
        Account acc1 = acF.createAccount("Svense", "test");
        acF.addCharacterToAccount(acc1, player1);
        //Setups second user
        Player player2 = new Player("Asger");
        Account acc2 = acF.createAccount("Asger", "test");
        acF.addCharacterToAccount(acc2, player2);
    }

    private static void setupPlayerData(EntityManagerFactory _emf) throws AbilityNotFoundException, CharacterNotFoundException, PlayerNotFoundException {
        Player player1 = PlayerFacade.getPlayerFacade(_emf).getPlayerByID(1L);
        Player player2 = PlayerFacade.getPlayerFacade(_emf).getPlayerByID(2L);
        InventoryFacade.getInventoryFacade(_emf).setupInventory(player1);
        PlayerFacade.getPlayerFacade(_emf).addAbilityToPlayer(1L, AbilityFacade.getAbilityFacade(_emf).getAbilityByName("slam"));
        PlayerFacade.getPlayerFacade(_emf).addAbilityToPlayer(1L, AbilityFacade.getAbilityFacade(_emf).getAbilityByName("heal"));

        InventoryFacade.getInventoryFacade(_emf).setupInventory(player2);

        PlayerFacade.getPlayerFacade(_emf).addAbilityToPlayer(2L, AbilityFacade.getAbilityFacade(_emf).getAbilityByName("slam"));
        PlayerFacade.getPlayerFacade(_emf).addAbilityToPlayer(2L, AbilityFacade.getAbilityFacade(_emf).getAbilityByName("heal"));
    }

    private static void SetupPotions(PotionFacade potfc) {
        Potion hp1 = new Potion("Slight Healing Potion", "Restores 10 health", 10, 0, 0);
        Potion hp2 = new Potion("Small Healing Potion", "Restores 20 health", 20, 0, 0);
        Potion hp3 = new Potion("Medium Healing Potion", "Restores 30 health", 30, 0, 0);
        Potion hp4 = new Potion("Large Healing Potion", "Restores 40 health", 40, 0, 0);

        Potion mp1 = new Potion("Slight Mana Potion", "Restores 10 mana", 0, 10, 0);
        Potion mp2 = new Potion("Small Mana Potion", "Restores 20 mana", 0, 20, 0);
        Potion mp3 = new Potion("Medium Mana Potion", "Restores 30 mana", 0, 30, 0);
        Potion mp4 = new Potion("Large Mana Potion", "Restores 40 mana", 0, 40, 0);

        Potion ap1 = new Potion("Essence of the Monkey", "Increases your dmg by 2 for this encounter.", 0, 0, 2);
        Potion ap2 = new Potion("Essence of the Tiger", "Increases your dmg by 4 for this encounter.", 0, 0, 4);
        Potion ap3 = new Potion("Essence of the Gorilla", "Increases your dmg by 6 for this encounter.", 0, 0, 6);
        Potion ap4 = new Potion("Essence of the Titans", "Increases your dmg by 10 for this encounter.", 0, 0, 10);

        potfc.addPotion(hp1);
        potfc.addPotion(hp2);
        potfc.addPotion(hp3);
        potfc.addPotion(hp4);

        potfc.addPotion(mp1);
        potfc.addPotion(mp2);
        potfc.addPotion(mp3);
        potfc.addPotion(mp4);

        potfc.addPotion(ap1);
        potfc.addPotion(ap2);
        potfc.addPotion(ap3);
        potfc.addPotion(ap4);
    }

    private static void setupCharacterPotions(PlayerFacade pfc, InventoryFacade ifc) throws CharacterNotFoundException {
        DECharacter players1 = pfc.getPlayerByID(1L);
        DECharacter players2 = pfc.getPlayerByID(2L);
        ArrayList<Long> currentPotions = new ArrayList<Long>();
        currentPotions.add(1L);
        currentPotions.add(5L);
        currentPotions.add(9L);
        Inventory inventory1 = ifc.getInventory(players1, players1.getInventory().getId());
        Inventory inventory2 = ifc.getInventory(players2, players2.getInventory().getId());
        for(Long longs : currentPotions){
            inventory1.getPotionIds().add(longs);
            inventory2.getPotionIds().add(longs);
        }
        
        for(Long longs : inventory1.getPotionIds()){
            System.out.println(longs);
        }
        ifc.addToInventory(players1, inventory1);
        ifc.addToInventory(players2, inventory2);
    }

}

