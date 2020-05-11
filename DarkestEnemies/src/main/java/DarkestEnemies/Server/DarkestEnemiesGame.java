/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Server;

import DarkestEnemies.Entity.Ability;
import static DarkestEnemies.Entity.Ability_.player;
import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.HealthPotion;
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
import DarkestEnemies.exceptions.AbilityNotFoundException;
import DarkestEnemies.exceptions.PlayerNotFoundException;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.PlayerFacade;
import DarkestEnemies.exceptions.ItemNotFoundException;

/**
 *
 * @author Asger
 */
public class DarkestEnemiesGame implements ITextGame {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    //AccountFacade facade = AccountFacade.getAccountFacade(emf);
    InventoryFacade ifc = InventoryFacade.getInventoryFacade(emf);
    PlayerFacade pF = PlayerFacade.getPlayerFacade(emf);
    AbilityFacade abF = AbilityFacade.getAbilityFacade(emf);

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

    private void mainMenu(ITextIO[] players, List<DECharacter> playerEntities) throws ItemNotFoundException {

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
                            encounter(encounter, players, playerEntities, enemy);
                            break;
                        }

                    //Player chooses inventory
                    case 2:

                        for (int j = 0; j < playerEntities.get(i).getHealthpotion().size(); j++) {
                            players[i].put(playerEntities.get(i).getHealthpotion().get(j).getInfo());
                        }

                        List<String> inventoryOptions = Arrays.asList();
                        for (int j = 0; j < playerEntities.get(i).getHealthpotion().size(); j++) {
                            inventoryOptions.add("Use " + playerEntities.get(i).getHealthpotion().get(j).getName() + "with index" + j);
                        }
                        inventoryOptions.add("Go Back");

                        int inventoryChoice = players[i].select("Inventory", inventoryOptions, "");

                        switch (inventoryChoice) {

                            case 1:
                                players[i].put("Enter the index of the potion you wish to use.");
                                ifc.useHealthPotion(playerEntities.get(i), players[i].getInteger(0, inventoryOptions.size() - 1));
                                //playerEntities.get(i).getHealthpotion().use(playerEntities.get(i));
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
                    //Creates a new player, adds it to the database and writes it out to the IO
                    String playerName = pF.createNewPlayer(players[i]).getCharacterName();
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

    private void encounter(List<DECharacter> encounter, ITextIO[] players, List<DECharacter> playerEntities, NPC enemy) {
        players[i].clear();
        //Setups bools
        boolean playerAlive = true;
        boolean enemyAlive = true;
        //introduction to encounter
        for (int i = 0; i < players.length; i++) {
            players[i].clear();
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

                    //Creates a new list of all the available targets of the ability
                    ArrayList<Integer> targetsIndex = new ArrayList();
                    int targetIndex = players[i].select("Who do you wish to target?", names, "");
                    for (int j = 0; j < encounter.get(j).getAbilities().get(choice - 1).getAmountOfTargets(); ++j) {
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
                            players[i].put("Hit! " + target.getCharacterName() + " now has " + target.getHealth() + " HP left!\n\n");
                            target.setHealth(target.getHealth() + chosenAbility.getHealing());
                        } else if (chosenAbility.getHealing() <= 0 && chosenAbility.getDamage() > 0) {
                            players[i].put("Hit! " + target.getCharacterName() + " now has " + target.getHealth() + " HP left!\n\n");
                            target.setHealth(target.getHealth() - chosenAbility.getDamage() + playerEntities.get(i).getAttackDmg());
                        }
                    }

                }

                //Checks HP for NPC
                if (enemy.getHealth() <= 0) {
                    enemyAlive = false;
                    for (int j = 0; j < players.length; j++) {
                        players[i].put("You did killed the enemy");
                        ifc.addHealthPotion(playerEntities.get(j), "small health potion", 10);
                        Long potionRank =(long) playerEntities.get(j).getLevel();
                        HealthPotion hp = ifc.getHealthPotionByID(potionRank);
                        ifc.addPotionToPlayer(playerEntities.get(j).getId(), hp);
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
