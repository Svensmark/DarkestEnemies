/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.Entity.NPC;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.textio.ITextIO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Asger
 */
public class DarkestEnemiesGame implements ITextGame {

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public void startGame(ITextIO[] players) {
        String[] names = new String[players.length];
        List<DECharacter> playerEntities = new ArrayList();

        while (true) {

            playerSetup(players, names, playerEntities);

            //Start Announcement
            for (int i = 0; i < players.length; i++) {
                players[i].put("Alrighty! Let's get started!\n");
            }

            //enemy setup
            NPC enemy = new NPC("Goblin", 10, 0, 5);

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
                    System.out.println(choice);
                    switch (choice) {

                        //Attack
                        case 1:
                            enemy.setHealth(enemy.getHealth() - playerEntities.get(i).getAttackDmg());
                            players[i].clear();
                            players[i].put("Hit! " + enemy.getCharacterName() + " now has " + enemy.getHealth() + " left!\n\n");
                            break;
                        //Heal
                        case 2:
                            if (playerEntities.get(i).getHealth() > 90) {
                                playerEntities.get(i).setHealth(100);
                                System.out.println("Healed some small ammount");
                                break;
                            } else {
                                playerEntities.get(i).setHealth(playerEntities.get(i).getHealth() + 10);
                                players[i].clear();
                                System.out.println("Healed for 10!");
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

    private void playerSetup(ITextIO[] players, String[] names, List<DECharacter> playerEntities) {
        //Player setup
        System.out.println("Welcome! We're just getting everyone set up. Please type in the information propmpted to you or wait your turn.\n");
        for (int i = 0; i < players.length; i++) {
            players[i].put("Let's start with your name!\nWhat do you wish to be called?:");
            names[i] = players[i].get();
            Player p = new Player(names[i], 100, 0, 2);
            playerEntities.add(p);
        }
    }

}
