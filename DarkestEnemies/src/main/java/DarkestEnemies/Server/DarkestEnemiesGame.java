/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.Entity.Ability;

//HVAD ER DEN HER TIL EMIL???
//import static DarkestEnemies.Entity.Ability_.player; 
import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.Potion;
import DarkestEnemies.Entity.NPC;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.exceptions.AccountNotFoundException;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.PotionFacade;
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
import DarkestEnemies.exceptions.AbilityNotFoundException;
import DarkestEnemies.exceptions.PlayerNotFoundException;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.PlayerFacade;
import DarkestEnemies.exceptions.ItemNotFoundException;
import DarkestEnemies.facades.InventoryFacade;

/**
 *
 * @author Asger
 */
public class DarkestEnemiesGame implements ITextGame {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    //AccountFacade facade = AccountFacade.getAccountFacade(emf);
    PotionFacade pfc = PotionFacade.getInventoryFacade(emf);
    PlayerFacade pF = PlayerFacade.getPlayerFacade(emf);
    AbilityFacade abF = AbilityFacade.getAbilityFacade(emf);
    InventoryFacade ifc = InventoryFacade.getInventoryFacade(emf);

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public void startGame(ITextIO[] players) {

        //Sets up base abilities in the database if needed
        abF.setupBasicAbilities();

        //Setups a list of players
        List<DECharacter> playerEntities = playerSetup(players);

        //Main loop
        while (true) {

            try {
                //Main menu
                mainMenu(players, playerEntities);
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGame.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    //First method called - sets up players 
    private List<DECharacter> playerSetup(ITextIO[] players) {
        List<DECharacter> playerEntities = new ArrayList();
        //Player setup
        for (int i = 0; i < players.length; i++) {

            //Login & Create account
            List<String> options = Arrays.asList("Login", "Create account");
            players[i].clear();
            int option = players[i].select("Please choose an option", options, "");

            switch (option) {

                //User chooses to login
                case 1:
                    i = login(players, i, playerEntities);
                    break;

                //User chooses to create account    
                case 2:
                    createAccount(players[i]);
                    i--;
                    break;
            }
        }
        return playerEntities;
    }

    private void mainMenu(ITextIO[] playersIO, List<DECharacter> players) throws ItemNotFoundException {

        //Start Announcement
        for (int i = 0; i < playersIO.length; i++) {
            //Clears the screen
            playersIO[i].clear();

            //The player in players list is in menu
            boolean menu = true;
            while (menu) {
                List<String> options = Arrays.asList("Ready to find enemy", "Inventory", "Log out");
                int menuChoice = playersIO[i].select("Main menu", options, "");
                switch (menuChoice) {

                    //User chooses to find an enemy
                    case 1:
                        if (i != playersIO.length - 1) {
                            menu = false;
                            break;
                        } else {
                            //enemy setup
                            NPC enemy = createNPC(players);

                            //encounter setup
                            List<DECharacter> allCharacters = new ArrayList();
                            List<DECharacter> enemies = new ArrayList();
                            for (int j = 0; j < playersIO.length; j++) {
                                allCharacters.add(players.get(j));
                            }
                            allCharacters.add(enemy);
                            enemies.add(enemy);

                            //Encounter
                            encounter(playersIO, allCharacters, players, enemies, true);
                            i--;
                            break;
                        }

                    //Player chooses inventory
                    case 2:

                        //All the possible actions the user can take will be placed here.
                        ArrayList<String> actions = new ArrayList();
                        List<Long> potionIds = players.get(i).getInventory().getPotionIds();
                        for (Long longs : potionIds) {
                            actions.add(pfc.getPotionByID(longs).getName() + " - " + pfc.getPotionByID(longs).getInfo());
                        }

                        int choice = playersIO[i].select("Which potion do you wish to use?", actions, "");
                        Potion chosen = pfc.getPotionByID(potionIds.get(choice - 1));
                        pfc.usePotion(players.get(i), chosen);
                        ifc.removeFromInventory(players.get(i), choice - 1);
                        break;
                    //Player logs out
                    case 3: {
                        try {
                            playersIO[i + 1].close();
                            playersIO[i].close();
                        } catch (IOException ex) {
                            Logger.getLogger(DarkestEnemiesGame.class.getName()).log(Level.SEVERE, null, ex);
                            playersIO[i].put("Something went wrong.");
                        }
                    }
                }
            }
        }
    }

    private int login(ITextIO[] players, int i, List<DECharacter> playerEntities) {
        try {
            AccountFacade af = AccountFacade.getAccountFacade(emf);
            players[i].put("Usename:");
            String username = players[i].get();
            players[i].put("Password:");
            String password = players[i].get();
            DECharacter player = af.login(username, password);

            //If nothing went wrong the logged in player is added to the list
            playerEntities.add(player);

        } catch (AccountNotFoundException e) {
            players[i].put("Something went wrong - couldn't find an account with that username \n");
            i--;
        } catch (WrongPasswordException e) {
            players[i].put("The password does not match that username \n");
            i--;
        }
        return i;
    }

    private void createAccount(ITextIO playerIO) {
        //Creates a new player, adds it to the database and writes it out to the IO
        String playerName = pF.createNewPlayer(playerIO).getCharacterName();
        Player player = null;
        try {
            player = pF.getPlayerByName(playerName);
        } catch (PlayerNotFoundException e) {
            playerIO.put("Something went wrong with finding the player by name: " + playerName);
        }

        //Adds the start ability to the player "slam"
        try {
            pF.addAbilityToPlayer(player.getId(), abF.getAbilityByName("slam"));
            pF.addAbilityToPlayer(player.getId(), abF.getAbilityByName("heal"));
        } catch (AbilityNotFoundException e) {
            playerIO.put("Something went wrong with getting the start abilities");
        }
    }

    private NPC createNPC(List<DECharacter> playerEntities) {
        //Initizialation of stats
        int health = 0;
        int mana = 0;
        int attack = 0;

        //Scales the health up for each player in the group
        for (int i = 0; i < playerEntities.size(); i++) {
            health += (playerEntities.get(i).getLevel() * 5) + (playerEntities.get(i).getAttackDmg() * 2.5);
        }

        //Scales the attack damage up for each player in the group
        for (int i = 0; i < playerEntities.size(); i++) {
            attack += ((playerEntities.get(i).getLevel() * 5) / 2.5) + (health / 10);
        }

        //Creates a random name from elder scrolls universe
        Faker faker = new Faker();
        String name = faker.elderScrolls().creature();

        return new NPC(name, health, mana, attack);
    }

    private void encounter(ITextIO[] playersIO, List<DECharacter> allCharacters, List<DECharacter> team1, List<DECharacter> team2, boolean pve) throws ItemNotFoundException {
        //Setups bools
        boolean team1Alive = true; //Always players when PVE - Always players when PVP
        boolean team2Alive = true; //Always hostile NPC when PVE - Always players when PVP

        //Introduction to encounter if it is a PVE matchup
        if (pve) {
            pveIntro(playersIO, team2);
        } else { //Introduction to encounter if it is a PVP matchup 
        }

        //While both teams are alive the encounter is ongoing (keeps looping)
        while (team1Alive == true && team2Alive == true) {
            //Each character has a turn
            for (int i = 0; i < allCharacters.size(); i++) {

                //If the the character is an NPC                               
                if (allCharacters.get(i).getClass() == NPC.class) {
                    npcAction(playersIO, team1, (NPC) allCharacters.get(i));
                }

                //If the character is a player
                if (allCharacters.get(i).getClass() != NPC.class) {
                    playerAction(allCharacters, i, playersIO, team1);
                }

                //Checks HP for all team2 members
                for (DECharacter team1character : team1) {
                    //Enters if-statement if a member of team 2 is dead
                    if (team1character.getHealth() <= 0) {
                        //Removes the dead member
                        team1.remove(team1character);
                        //Checks if the team is empty - if so, team 1 wins
                        if (team1.isEmpty()) {
                            team1Alive = false;
                            for (int j = 0; j < playersIO.length; j++) {
                                playersIO[j].put("You have all been killed by the enemy!");
                            }
                            break;
                        }
                    }
                }

                //Checks HP for all team2 members
                for (DECharacter team2character : team2) {
                    //Enters if-statement if a member of team 2 is dead
                    if (team2character.getHealth() <= 0) {
                        //Removes the dead member
                        team2.remove(team2character);
                        //Checks if the team is empty - if so, team 1 wins
                        if (team2.isEmpty()) {
                            team2Alive = false;
                            for (int j = 0; j < playersIO.length; j++) {
                                playersIO[j].put("You killed the enemy! \n");

                                //Rewards should be a new method
                                Long potionRank = (long) team1.get(j).getLevel();
                                Potion hp = pfc.getPotionByID(potionRank);
//                                pfc.addPotionToPlayer(team1.get(j).getId(), hp);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void pveIntro(ITextIO[] playersIO, List<DECharacter> team2) {
        for (int i = 0; i < playersIO.length; i++) {
            playersIO[i].clear();
            playersIO[i].put("You've encountered the following: \n");
            for (DECharacter enemy : team2) {
                printNPCStats(playersIO[i], enemy);
            }
        }
    }

    private void printNPCStats(ITextIO playerIO, DECharacter npc) {
        playerIO.put(npc.getName() + "\n");
        playerIO.put("- " + npc.getHealth() + " HP \n");
        playerIO.put("- " + npc.getAttackDmg() + " ATK \n\n");
    }

    private void npcAction(ITextIO[] playersIO, List<DECharacter> opposingTeam, NPC npc) {
        for (int j = 0; j < playersIO.length; j++) {
            opposingTeam.get(j).setHealth(opposingTeam.get(j).getHealth() - npc.getAttackDmg());
            playersIO[j].put(npc.getName() + " has hit you for " + npc.getAttackDmg() + ". \nYou now have " + opposingTeam.get(j).getHealth() + " hp left!\n\n");
            printNPCStats(playersIO[j], npc);
        }
    }

    private void playerAction(List<DECharacter> encounter, int i, ITextIO[] players, List<DECharacter> playerEntities) {
        //Creates an empty list of available actions
        ArrayList<String> actions = new ArrayList();

        //Gets the abilities of the current player
        List<Ability> abilities = encounter.get(i).getAbilities();

        //Adds the abils name to actions list so it can be selected
        for (Ability ab : abilities) {
            actions.add(ab.getName() + "\n" + "  - " + ab.getDescription());
        }
        int choice = players[i].select("What do you wish to do?", actions, "");

        //Gets the chosen ability
        Ability chosenAbility = encounter.get(i).getAbilities().get(choice - 1);

        //Creates a new list of all the names in the encounter
        ArrayList<String> names = new ArrayList();
        ArrayList<DECharacter> availableTargets = new ArrayList();
        for (int j = 0; j < encounter.size(); j++) {
            names.add(encounter.get(j).getName());
            availableTargets.add(encounter.get(j));
        }

        //Creates a new list of all the chosen targets of the ability
        ArrayList<Integer> targetsIndex = new ArrayList();
        int targetIndex = players[i].select("Who do you wish to target?", names, "");
        for (int j = 0; j < encounter.get(i).getAbilities().get(choice - 1).getAmountOfTargets(); ++j) {
            targetsIndex.add(targetIndex);
        }

        //Creates a list of DECharaters that is the actual targets
        ArrayList<DECharacter> targets = new ArrayList();
        for (int j = 0; j < targetsIndex.size(); ++j) {
            int index = targetsIndex.get(j);
            DECharacter targ = availableTargets.get(index - 1);
            targets.add(targ);
        }

        for (DECharacter target : targets) {
            if (chosenAbility.getDamage() <= 0 && chosenAbility.getHealing() > 0) {
                target.setHealth(target.getHealth() + chosenAbility.getHealing());
                players[i].put("Hit! " + target.getCharacterName() + " now has " + target.getHealth() + " HP left!\n\n");
            } else if (chosenAbility.getHealing() <= 0 && chosenAbility.getDamage() > 0) {
                target.setHealth(target.getHealth() - chosenAbility.getDamage() + playerEntities.get(i).getAttackDmg());
                players[i].put("Hit! " + target.getCharacterName() + " now has " + target.getHealth() + " HP left!\n\n");
            }
        }

        players[i].put("Press enter to continue");
        players[i].get();
        players[i].clear();
        players[i].put("Waiting for players.. \n");
    }

}
