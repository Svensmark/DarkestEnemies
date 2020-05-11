/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.NPC;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.exceptions.AccountNotFoundException;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.InventoryFacade;
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
import DarkestEnemies.IF.DECharacter;

/**
 *
 * @author Asger
 */
public class DarkestEnemiesGame implements ITextGame {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    //AccountFacade facade = AccountFacade.getAccountFacade(emf);
    InventoryFacade ifc = InventoryFacade.getInventoryFacade(emf);

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public void startGame(ITextIO[] players) {

        //Setups a list of players
        List<DECharacter> playerEntities = playerSetup(players);

        //Main loop
        while (true) {

            //Main menu
            mainMenu(players, playerEntities);

        }
    }

    private void mainMenu(ITextIO[] players, List<DECharacter> playerEntities) {

        //Start Announcement
        for (int i = 0; i < players.length; i++) {

            //The player in players list is in menu
            boolean menu = true;
            while (menu) {
                List<String> options = Arrays.asList("Ready to find enemy", "Inventory", "Log out");
                int menuChoice = players[i].select("Main menu", options, "");
                switch (menuChoice) {

                    //User chooses to find an enemy
                    case 1:
                        if (i != players.length - 1) {
                            menu = false;
                            break;
                        } else {
                            //enemy setup
                            NPC enemy = enemySetup(playerEntities, players);

                            //encounter setup
                            List<DECharacter> encounter = new ArrayList();
                            for (int j = 0; j < players.length; j++) {
                                encounter.add(playerEntities.get(j));
                            }
                            encounter.add(enemy);

                            //Encounter
                            firstEncounter(encounter, players, playerEntities, enemy);
                            break;
                        }

                    //Player chooses inventory
                    case 2:
                        players[i].put("Not implemented yet");
                        playerEntities.get(i).getHealthpotion().getInfo();
                        playerEntities.get(i).getHealthpotion().getId();
                        List<String> inventoryOptions = Arrays.asList("Use potion", "Go back");
                        int inventoryChoice = players[i].select("Inventory", inventoryOptions, "");
                        switch (inventoryChoice) {
                            case 1:
                                ifc.useHealthPotion(playerEntities.get(i));
                                playerEntities.get(i).getHealthpotion().use(playerEntities.get(i));
                                break;
                            case 2:
                                break;
                        }
                        break;
                    //Player logs out
                    case 3: {
                        try {
                            players[i + 1].close();
                            players[i].close();
                        } catch (IOException ex) {
                            Logger.getLogger(DarkestEnemiesGame.class.getName()).log(Level.SEVERE, null, ex);
                            players[i].put("Something went wrong.");
                        }
                    }
                }
            }
        }
    }

    private List<DECharacter> playerSetup(ITextIO[] players) {
        List<DECharacter> playerEntities = new ArrayList();
        //Player setup
        for (int i = 0; i < players.length; i++) {

            //Login & Create account
            List<String> options = Arrays.asList("Login", "Create account");
            int option = players[i].select("Please choose an option", options, "");
            switch (option) {

                //User chooses to login
                case 1:
                    boolean login = true;
                    while (login) {
                        try {
                            AccountFacade af = AccountFacade.getAccountFacade(emf);
                            players[i].put("Usename:");
                            String username = players[i].get();
                            players[i].put("Password:");
                            String password = players[i].get();
                            DECharacter player = af.login(username, password);

                            //If nothing went wrong the logged in player is added to the list
                            playerEntities.add(player);
                            login = false;
                        } catch (AccountNotFoundException e) {
                            players[i].put("Something went wrong, please try again (user came back as null)");
                            e.printStackTrace();
                        } catch (WrongPasswordException e) {
                            players[i].put("Username and password does not match, please try again");
                            e.printStackTrace();
                        }
                    }
                    break;

                //User chooses to create account    
                case 2:
                    AccountFacade af = AccountFacade.getAccountFacade(emf);
                    players[i].put("New usename:");
                    String username = players[i].get();
                    players[i].put("New password:");
                    String password = players[i].get();
                    Account a = af.createAccount(username, password);
                    players[i].put("Character name:");
                    String characterName = players[i].get();
                    af.addCharacterToAccount(a, new Player(characterName));
                    players[i].put("Succes - you can now login with your account");
                    i--;
                    break;
            }
        }
        return playerEntities;
    }

    private NPC enemySetup(List<DECharacter> playerEntities, ITextIO[] players) {
        int health = 0;
        int mana = 0;
        int attack = 0;

        for (int i = 0; i < players.length; i++) {
            health += (playerEntities.get(i).getLevel() * 5) + (playerEntities.get(i).getAttackDmg() * 2.5);
        }

        for (int i = 0; i < players.length; i++) {
            attack += ((playerEntities.get(i).getLevel() * 5) / 2.5) + (health / 10);
        }

        Faker faker = new Faker();
        String name = faker.elderScrolls().creature();
        return new NPC(name, health, mana, attack);
    }

    private void firstEncounter(List<DECharacter> encounter, ITextIO[] players, List<DECharacter> playerEntities, NPC enemy) {
        //Setups bools
        boolean playerAlive = true;
        boolean enemyAlive = true;
        //introduction to encounter
        for (int i = 0; i < players.length; i++) {
            players[i].put("Oh no! You've encountered a " + enemy.getCharacterName() + "!\n");
            players[i].put("This is your first encounter, should be easy. Just wack a mole him!\n");
        }

        while (playerAlive == true && enemyAlive == true) {

            //Encounter START
            for (int i = 0; i < encounter.size(); i++) {

                System.out.println(encounter.get(i).getClass());
                //If the the character is an NPC
                if (encounter.get(i).getClass() == NPC.class) {

                    System.out.println("The NPC has taken their turn");
                    for (int j = 0; j < players.length; j++) {
                        //System monitoring.
                        System.out.println("current player hp: " + playerEntities.get(j).getHealth());
                        System.out.println("current player hp: " + playerEntities.get(j).getAttackDmg());
                        System.out.println("enemy hp: " + enemy.getHealth());
                        System.out.println("enemy atkdmg: " + enemy.getAttackDmg());

                        playerEntities.get(j).setHealth(playerEntities.get(j).getHealth() - enemy.getAttackDmg());
                        players[j].put("You've been hit! You now have: " + playerEntities.get(j).getHealth() + " hp left!\n");
                    }
                }

                //If the character is a player
                if (encounter.get(i).getClass() != NPC.class) {
                    //Creates of list
                    ArrayList<String> actions = new ArrayList();

                    //Gets the abilities of the current player
                    List<Ability> abilities = encounter.get(i).getAbilities();

                    //Adds the abils name to actions list so it can be selected
                    for (Ability ab : abilities) {
                        actions.add(ab.getName() + "\n" + " - " + ab.getDescription());
                    }
                    int choice = players[i].select("What do you wish to do?", actions, "");
                    
                    Ability chosenAbility = encounter.get(i).getAbilities().get(choice);

                    //Creates a new list of all the names in the encounter
                    ArrayList<String> names = new ArrayList();
                    ArrayList<DECharacter> targets = new ArrayList();
                    for (int j = 0; j < encounter.size(); j++) {
                        names.add(encounter.get(j).getName());
                        targets.add(encounter.get(j));
                    }

                    //Creates a new list of all the targets of the ability
                    ArrayList<Integer> targetsIndex = new ArrayList();
                    for (int j = 0; j < encounter.get(j).getAbilities().get(choice).getAmountOfTargets(); ++j) {
                        int targetIndex = players[i].select("Who do you wish to target?", names, "");
                        targetsIndex.add(targetIndex);
                    }
                    
                    for (DECharacter target : targets) {
                        if (chosenAbility.getDamage() <= 0 && chosenAbility.getHealing() > 0) {
                            target.setHealth(target.getHealth() + chosenAbility.getHealing());
                        } else if (chosenAbility.getHealing() <= 0 && chosenAbility.getDamage() > 0) {
                            target.setHealth(target.getHealth() - chosenAbility.getDamage() + playerEntities.get(i).getAttackDmg());
                        }
                        
                    }

                    /*
                    switch (choice) {

                        //Attack
                        case 1:
                            enemy.setHealth(enemy.getHealth() - playerEntities.get(i).getAttackDmg());
                            players[i].clear();
                            players[i].put("Hit! " + enemy.getCharacterName() + " now has " + enemy.getHealth() + " left!\n\n");
                            break;
                        //Heal
                        case 2:
                            if (playerEntities.get(i).getHealth() > 9) {
                                playerEntities.get(i).setHealth(10);
                                System.out.println("Healed some small ammount");
                                break;
                            } else {
                                playerEntities.get(i).setHealth(playerEntities.get(i).getHealth() + 1);
                                players[i].clear();
                                System.out.println("Healed for 1!");
                                break;
                            }
                    }*/
                }

                //Checks HP for NPC
                if (enemy.getHealth() <= 0) {
                    System.out.println("Well you've diddley done it! Congrats!");
                    enemyAlive = false;
                    for (int j = 0; j < players.length; j++) {
                        ifc.addHealthPotion(playerEntities.get(j), "small health potion", 10);
                    }
                    break;
                }

                //Checks HP for Players
                for (int j = 0; j < players.length; ++j) {
                    if (playerEntities.get(j).getHealth() <= 0) {
                        players[j].put("Oh no! You've been killed! Game over!\n");
                        players[j + 1].put("Oh no!" + playerEntities.get(j).getCharacterName() + " has been killed! You flee in fear. Game over!\n");
                        playerAlive = false;
                        break;
                    }
                }
            }
        }
    }

}
