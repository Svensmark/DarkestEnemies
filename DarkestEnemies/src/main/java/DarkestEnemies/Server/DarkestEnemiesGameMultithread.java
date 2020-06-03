/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.Entity.NPC;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.AbilityNotFoundException;
import DarkestEnemies.exceptions.AccountNotFoundException;
import DarkestEnemies.exceptions.ItemNotFoundException;
import DarkestEnemies.exceptions.PlayerNotFoundException;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.InventoryFacade;
import DarkestEnemies.facades.PlayerFacade;
import DarkestEnemies.syncbox.SyncBox;
import DarkestEnemies.textio.ITextIO;
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
    InventoryFacade ifc = InventoryFacade.getInventoryFacade(emf);
    PlayerFacade pF = PlayerFacade.getPlayerFacade(emf);
    AbilityFacade abF = AbilityFacade.getAbilityFacade(emf);

    @Override
    public void startGame(ITextIO player, ArrayList<SyncBox> syncBoxes) {
        //Sets up base abilities in the database if needed
        abF.setupBasicAbilities();

        //Asking wether
        DECharacter playerCharacter = playerSetup(player);

        //Main loop
        while (true) {
            mainMenu(player, playerCharacter);
        }

    }

    private DECharacter playerSetup(ITextIO playerIO) {
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

    private void mainMenu(ITextIO playerIO, DECharacter playerCharacter) {

        //The player in players list is in menu
        boolean menu = true;
        while (menu) {
            List<String> options = Arrays.asList("Find enemy", "Inventory", "Log out");
            int menuChoice = playerIO.select("Main menu", options, "");
            switch (menuChoice) {

                //User chooses to find an enemy
                case 1: //Something here with syncboxes

                //Player chooses inventory
                case 2:
                    for (int j = 0; j < playerCharacter.getHealthpotion().size(); j++) {
                        playerIO.put(playerCharacter.getHealthpotion().get(j).getInfo());
                    }

                    ArrayList<String> inventoryOptions = new ArrayList();
                    for (int j = 0; j < playerCharacter.getHealthpotion().size(); j++) {
                        inventoryOptions.add("Use " + playerCharacter.getHealthpotion().get(j).getName() + "with index" + j);
                    }
                    inventoryOptions.add("Go Back");

                    //Only 1 option (The Go Back option, which means inventory is empty
                    if (inventoryOptions.size() == 1) {
                        int inventoryChoice = playerIO.select("Empty Inventory", inventoryOptions, "");
                        switch (inventoryChoice) {
                            case 1:
                                break;
                        }
                        break;
                    } else { //If theres more than 1 option, the inventory isn't empty
                        int inventoryChoice = playerIO.select("Inventory", inventoryOptions, "");
                        switch (inventoryChoice) {
                            case 1:
                                playerIO.put("Enter the index of the potion you wish to use.");
                                ifc.useHealthPotion(playerCharacter, playerIO.getInteger(0, inventoryOptions.size() - 1));
                                //playerEntities.get(i).getHealthpotion().use(playerEntities.get(i));
                                break;
                            case 2:
                                break;
                        }
                        break;
                    }

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

}
