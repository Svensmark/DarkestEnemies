/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.NPC;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.AccountNotFoundException;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.textio.ITextIO;
import entities.exceptions.WrongPasswordException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

/**
 *
 * @author Asger
 */
public class DarkestEnemiesGame implements ITextGame {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/darkestenemies",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    //AccountFacade facade = AccountFacade.getAccountFacade(emf);

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

            //Start Announcement
            for (int i = 0; i < players.length; i++) {
                
                //The player in players list is in menu
                boolean menu = true;
                while (menu) {
                    List<String> options = Arrays.asList("Find enemy", "Inventory", "Log out");
                    int menuChoice = players[i].select("Main menu", options, "");
                    switch (menuChoice) {
                        //User chooses to find an enemy
                        case 1:
                            menu = false;
                            break;
                        
                        //Player chooses inventory
                        case 2:
                            players[i].put("Not implemented yet");
                            
                        //Player logs out
                        case 3: 
                            players[i].put("Not implemented yet");
                    }
                }
            }

            //enemy setup
            NPC enemy = new NPC("Goblin", 10, 0, 1);

            //encounter setup
            List<DECharacter> encounter = new ArrayList();
            for (int i = 0; i < players.length; i++) {
                encounter.add(playerEntities.get(i));
            }
            encounter.add(enemy);

            //Encounter
            firstEncounter(encounter, players, playerEntities, enemy);

            //Breaks out of the main loop
            System.out.println("Game Complete!");
            break;
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

    private void firstEncounter(List<DECharacter> encounter, ITextIO[] players, List<DECharacter> playerEntities, NPC enemy) {
        //Setups bools
        boolean playerAlive = true;
        boolean enemyAlive = true;

        //introduction to encounter
        for (int i = 0; i < players.length; i++) {
            players[i].put("Oh no! You've encountered a goblin!\n");
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
                        System.out.println("current hp " + playerEntities.get(j).getHealth());
                        System.out.println("enemy atkdmg " + enemy.getAttackDmg());
                        System.out.println(playerEntities.get(j).getHealth() - enemy.getAttackDmg());
                        playerEntities.get(j).setHealth(playerEntities.get(j).getHealth() - enemy.getAttackDmg());
                        players[j].put("You've been hit! You now have: " + playerEntities.get(j).getHealth() + " hp left!\n");
                    }
                }

                //If the character is a player
                if (encounter.get(i).getClass() != NPC.class) {
                    //Creates of list
                    ArrayList<String> actions = new ArrayList();
                    //Adds the available actions of the player to the list
                    actions.add("Attack");
                    actions.add("Heal");

                    int choice = players[i].select("What do you wish to do?", actions, "");
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
                    }
                }

                //Checks HP for NPC
                if (enemy.getHealth() <= 0) {
                    System.out.println("Well you've diddley done it! Congrats!");
                    enemyAlive = false;
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
