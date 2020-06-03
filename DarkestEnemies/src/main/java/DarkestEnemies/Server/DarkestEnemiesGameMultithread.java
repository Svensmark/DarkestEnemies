/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Inventory;
import DarkestEnemies.Entity.NPC;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.Entity.Potion;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.AbilityNotFoundException;
import DarkestEnemies.exceptions.AccountNotFoundException;
import DarkestEnemies.exceptions.ItemNotFoundException;
import DarkestEnemies.exceptions.PlayerNotFoundException;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.InventoryFacade;
import DarkestEnemies.facades.PlayerFacade;
import DarkestEnemies.facades.PotionFacade;
import DarkestEnemies.syncbox.SyncBox;
import DarkestEnemies.textio.ITextIO;
import com.github.javafaker.Faker;
import entities.exceptions.WrongPasswordException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

/**
 *
 * @author Gamer
 */
public class DarkestEnemiesGameMultithread implements ITextGameMultithread {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    PotionFacade pfc = PotionFacade.getInventoryFacade(emf);
    InventoryFacade ifc = InventoryFacade.getInventoryFacade(emf);
    PlayerFacade pF = PlayerFacade.getPlayerFacade(emf);
    AbilityFacade abF = AbilityFacade.getAbilityFacade(emf);

    @Override
    public void startGame(ITextIO playerIO, SyncBox allSyncBoxes) {
        //Sets up base abilities in the database if needed
        abF.setupBasicAbilities();

        //Asking wether
        DECharacter playerCharacter = login(playerIO);

        //Main loop
        while (true) {
            mainMenu(playerIO, playerCharacter, allSyncBoxes);
        }

    }

    private DECharacter login(ITextIO playerIO) {
        DECharacter playerCharacter = null;

        //Login & Create account
        List<String> options = Arrays.asList("Login", "Create account");

        //While the playerIO is setting up their account/logging in
        boolean settingUp = true;
        while (settingUp) {
            int option = playerIO.select("Please choose an option", options, "");

            switch (option) {
                //User chooses to login
                case 1:
                    boolean login = true;
                    while (login) {
                        try {
                            AccountFacade af = AccountFacade.getAccountFacade(emf);
                            playerIO.put("Usename:");
                            String username = playerIO.get();
                            playerIO.put("Password:");
                            String password = playerIO.get();
                            playerCharacter = af.login(username, password);
                            //If nothing went wrong the logged in player is added to the list
                            login = false;
                        } catch (AccountNotFoundException e) {
                            playerIO.put("Something went wrong, please try again (user came back as null)");
                            e.printStackTrace();
                        } catch (WrongPasswordException e) {
                            playerIO.put("Username and password does not match, please try again");
                            e.printStackTrace();
                        }
                    }
                    settingUp = false;
                    break;

                //User chooses to create account    
                case 2:
                    //Creates a new player, adds it to the database and writes it out to the IO
                    String playerName = pF.createNewPlayer(playerIO).getCharacterName();
                    Player player = null;
                    try {
                        player = pF.getPlayerByName(playerName);
                    } catch (PlayerNotFoundException e) {
                        System.out.println("Something went wrong with finding the player by name: " + playerName);
                    }

                    //Adds the start ability to the player "slam"
                    try {
                        pF.addAbilityToPlayer(player.getId(), abF.getAbilityByName("slam"));
                    } catch (AbilityNotFoundException e) {
                        System.out.println("Something went wrong with getting the SLAM ability");
                    }
                    break;
            }
        }
        return playerCharacter;
    }

    private void mainMenu(ITextIO playerIO, DECharacter playerCharacter, SyncBox allSyncBoxes) {

        //The player in players list is in menu
        boolean menu = true;
        while (menu) {
            List<String> options = Arrays.asList("Find enemy", "Inventory", "Log out");
            int choice = playerIO.select("Main menu", options, "");
            switch (choice) {

                //User chooses to find an enemy
                case 1: //Something here with syncboxes
                    options = Arrays.asList("Solo", "Multiplayer");
                    choice = playerIO.select("How do you wish to play", options, "");

                    switch (choice) {
                        case 1: //Player wishes to play solo
                            
                            break;

                        case 2: //Player wishes to play multiplayer

                            break;
                    }

                //Player chooses inventory
                case 2:
                    showInventory(playerCharacter, playerIO);
                    break;

                //Player logs out
                case 3: {
                    try {
                        playerIO.put("You have been logged out!");
                        playerIO.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DarkestEnemiesGame.class.getName()).log(Level.SEVERE, null, ex);
                        playerIO.put("Something went wrong.");
                    }
                }
            }
        }
    }

    private void showInventory(DECharacter player, ITextIO playerIO) {
        //All the possible actions the user can take will be placed here.
        ArrayList<String> actions = new ArrayList();
        //List of all the potions the current player has
        List<Long> potionIds = new ArrayList();
        Inventory inventory = ifc.getInventory(player, player.getInventory().getId());
        potionIds = inventory.getPotionIds();
        //All of the possible actions get added
        //The actions include the name of the potion together with its description.
        for (Long longs : potionIds) {
            try {
                actions.add(pfc.getPotionByID(longs).getName() + " - " + pfc.getPotionByID(longs).getInfo());
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        actions.add("Return to menu");
        //Gets player input
        int choice = playerIO.select("Which potion do you wish to use?", actions, "");
        //Gets selected potion from the database.
        System.out.println(actions.size());
        if (choice != actions.size()) {
            Potion chosen = null;
            try {
                chosen = pfc.getPotionByID(potionIds.get(choice - 1));
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Consumes potion and removes it from the players inventory.
            pfc.usePotion(player, chosen);
            ifc.removeFromInventory(player, choice - 1);
        }
    }
    
    private NPC createNPC(List<DECharacter> playerCharacters) {
        //Initizialation of stats
        int health = 0;
        int mana = 0;
        int attack = 0;

        //Scales the health up for each player in the group
        for (int i = 0; i < playerCharacters.size(); i++) {
            health += (playerCharacters.get(i).getLevel() * 5) + (playerCharacters.get(i).getAttackDmg() * 2.5);
        }

        //Scales the attack damage up for each player in the group
        for (int i = 0; i < playerCharacters.size(); i++) {
            attack += ((playerCharacters.get(i).getLevel() * 5) / 2.5) + (health / 10);
        }

        //Creates a random name from elder scrolls universe
        Faker faker = new Faker();
        String name = faker.elderScrolls().creature();

        return new NPC(name, health, mana, attack);
    }
    
}
