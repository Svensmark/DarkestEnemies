/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.Entity.NPC;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.textio.ITextIO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Asger
 */
public class DarkestEnemiesGame implements ITextGame{

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public void startGame(ITextIO[] players) {
        String[] names = new String[players.length];
        List<Player> entities = new ArrayList();
        boolean playerAlive = true;
        boolean enemyAlive = true;
        while(true){
            
            //Player setup
            System.out.println("Welcome! We're just getting everyone set up. Please type in the information propmpted to you or wait your turn.\n");
            for(int i = 0; i < players.length; i++){
                players[i].put("Let's start with your name!\nWhat do you wish to be called?:\n");
                names[i] = players[i].get();
                Player p = new Player(names[i], 100, 0, 2);
                entities.add(p);
            }
            
            //Start Announcement
            for(int i = 0; i < players.length; i++){
                players[i].put("Alrighty! Let's get started!\n");
            }
            
            //First encounter
            while(playerAlive == true || enemyAlive == true){
                //enemy setup
                NPC enemy = new NPC("Goblin", 10, 0, 5);
                
                //introduction to encounter
                for(int i = 0; i < players.length; i++){
                    players[i].put("Oh no! You've encountered a goblin!\n");
                    players[i].put("This is your first encounter, should be easy. Just wack a mole him!\n");
                }
                
                //encounter setup START
                List encounter = new ArrayList();
                for(int i = 0; i < players.length; i++){
                    encounter.add(players[i]);
                }
                encounter.add(enemy);
                //encounter setup END
                
                //Encounter START
                for(int i = 0; i < encounter.size() - 1; i++){
                    System.out.println(encounter.size());
                    if(encounter.get(i).getClass() == NPC.class){
                        
                        for(int j = 0; i < players.length; i++){
                            entities.get(j).setHealth(entities.get(i).getHealth() - enemy.getAttackDmg());
                            if(entities.get(j).getHealth() <= 0){
                                players[j].put("Oh no! You've been killed! Game over!\n");
                                players[j+1].put("Oh no!" + entities.get(j).getCharacterName() + " has been killed! You flee in fear. Game over!\n");
                                playerAlive = false; 
                                break;
                            }
                            players[j].put("You've been hit! You now have: " + entities.get(j).getHealth() + " hp left!\n");
                        }
                    }
                    System.out.println(players[i]);
                    String action = players[i].get();
                    System.out.println(action);
                    players[i].put("What do you wish to do? press 1 and hit enter to attack, or 2 for heals");
                    switch(Integer.parseInt(action)){
                        //Attack
                        case 1:
                            enemy.setHealth(enemy.getHealth() - entities.get(i).getAttackDmg());
                            players[i].put("Hit! " + enemy.getCharacterName() + " now has " + enemy.getHealth() + " left!\n");
                        case 2:
                            if(entities.get(i).getHealth() > 90){
                                entities.get(i).setHealth(100);
                            }
                            entities.get(i).setHealth(entities.get(i).getHealth() + 10);
                    }
                    if(enemy.getHealth() <= 0){
                        System.out.println("Well you've diddley done it! Congrats!");
                        enemyAlive = false;
                        break;
                    }
                    
                }
                //Encounter END
            }
            
            System.out.println("Game Complete!");
            
            
                       
        }
        
    }
    
}
