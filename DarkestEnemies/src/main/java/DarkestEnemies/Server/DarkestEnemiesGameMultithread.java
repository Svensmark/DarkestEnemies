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
import DarkestEnemies.Entity.Trinket;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.AbilityNotFoundException;
import DarkestEnemies.exceptions.AccountNotFoundException;
import DarkestEnemies.exceptions.CharacterNotFoundException;
import DarkestEnemies.exceptions.ItemNotFoundException;
import DarkestEnemies.exceptions.PlayerNotFoundException;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.InventoryFacade;
import DarkestEnemies.facades.PlayerFacade;
import DarkestEnemies.facades.PotionFacade;
import DarkestEnemies.facades.TrinketFacade;
import DarkestEnemies.syncbox.SyncBox;
import DarkestEnemies.textio.ITextIO;
import HelpingClasses.HostingPlayer;
import HelpingClasses.JoiningPlayer;
import com.github.javafaker.Faker;
import entities.exceptions.WrongPasswordException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    TrinketFacade tfc = TrinketFacade.getInventoryFacade(emf);
    AsciiArt aa = new AsciiArt();

    @Override
    public void startGame(ITextIO playerIO, SyncBox allSyncBoxes) {
        //Sets up base abilities in the database if needed
        abF.setupBasicAbilities();

        //Asking wether
        DECharacter playerCharacter = introScreen(playerIO);

        //Main loop
        while (true) {
            mainMenu(playerIO, playerCharacter, allSyncBoxes);
        }

    }

    private DECharacter introScreen(ITextIO playerIO) {
        DECharacter playerCharacter = null;
        //Login & Create account
        List<String> options = Arrays.asList("Login", "Create account");

        //While the playerIO is setting up their account/logging in
        boolean settingUp = true;
        while (settingUp) {
            printServerIntroScreen(playerIO);
            int option = playerIO.select("                                                        [Login screen]", options, "");

            switch (option) {
                //User chooses to login
                case 1:
                    boolean login = true;
                    while (login) {
                        try {
                            AccountFacade af = AccountFacade.getAccountFacade(emf);
                            playerIO.put("Username:");
                            String username = playerIO.get();
                            playerIO.put("Password:");
                            String password = playerIO.get();
                            playerCharacter = af.login(username, password);
                            //If nothing went wrong the logged in player is added to the list
                            login = false;
                            settingUp = false;
                        } catch (AccountNotFoundException e) {
                            playerIO.put("We could not find an account with that name and password combination.\nPress enter to return ..");
                            playerIO.get();
                            login = false;
                            e.printStackTrace();
                        } catch (WrongPasswordException e) {
                            playerIO.put("We could not find an account with that name and password combination.\nPress enter to return ..");
                            playerIO.get();
                            login = false;
                            e.printStackTrace();
                        }
                    }
                    break;

                //User chooses to create account    
                case 2:
                    createAccount(playerIO);
                    break;
            }
        }
        return playerCharacter;
    }

    private void createAccount(ITextIO playerIO) {
        //Creates a new player, adds it to the database and writes it out to the IO
        String playerName = pF.createNewPlayer(playerIO).getCharacterName();
        Player player = null;
        try {
            player = pF.getPlayerByName(playerName);
        } catch (PlayerNotFoundException e) {
            System.out.println("Something went wrong with finding the player by name: " + playerName);
        }

        //Adds an inventory to the player
        try {
            ifc.setupInventory(player);
        } catch (PlayerNotFoundException ex) {
            System.out.println("Something went wrong with adding an inventory to the player");
        }

        //Adds the start ability to the player "slam"
        try {
            pF.addAbilityToPlayer(player.getId(), abF.getAbilityByName("slam"));
        } catch (AbilityNotFoundException e) {
            System.out.println("Something went wrong with getting the SLAM ability");
        }
    }

    private void printServerIntroScreen(ITextIO playerIO) {
        playerIO.clear();
        aa.printTitle(playerIO);
        playerIO.put("\n\n                                           Welcome to Darkest Enemies game server!\n\n");
    }

    private void mainMenu(ITextIO playerIO, DECharacter playerCharacter, SyncBox allSyncBoxes) {
        //The player in players list is in menu
        boolean menu = true;
        while (menu) {
            playerIO.clear();
            aa.printTitle(playerIO);
            List<String> options = Arrays.asList("Find dungeon", "Inventory", "Log out");
            int choiceMainMenu = playerIO.select("\n\n                                                        [Main menu]", options, "");
            switch (choiceMainMenu) {

                //User chooses to find an enemy
                case 1: //Something here with syncboxes
                    playerIO.clear();
                    aa.printRandom(playerIO);
                    options = Arrays.asList("Solo", "Multiplayer", "Go back");
                    int choiceFindEnemy = playerIO.select("\n                                                    [Game menu]", options, "");

                    switch (choiceFindEnemy) {
                        case 1: //Player wishes to play solo                             
                            singlePlayerDungeon(playerIO, playerCharacter);
                            break;

                        case 2: //Player wishes to play multiplayer                             
                            multiplayerMenu(playerIO, playerCharacter, allSyncBoxes);
                            break;

                        case 3: //Player wants to go back
                            break;
                    }
                    break;

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

    private void multiplayerMenu(ITextIO playerIO, DECharacter playerCharacter, SyncBox allSyncBoxes) {
        playerIO.clear();
        aa.printMultiplayer(playerIO);
        List<String> options = Arrays.asList("Host a room", "Join a room", "Go back");
        int choiceMultiplayer = playerIO.select("\n                                 [Multiplayer menu]", options, "");
        switch (choiceMultiplayer) {
            case 1://Player wishes to host a game
                hostMultiplayerGame(playerIO, playerCharacter, allSyncBoxes);
                break;

            case 2://Player wishes to join a hosted game
                joinMultiplayerGame(playerIO, playerCharacter, allSyncBoxes);
                break;

            case 3: //Player wants to go back
                break;
        }
    }

    private void hostMultiplayerGame(ITextIO playerIO, DECharacter playerCharacter, SyncBox allSyncBoxes) {
        HashMap allSyncBoxesHM = (HashMap) allSyncBoxes.peek();
        SyncBox hostingPlayersListSB = (SyncBox) allSyncBoxesHM.get("hostingPlayersList");
        ArrayList hostingPlayersList = (ArrayList) hostingPlayersListSB.peek();

        playerIO.clear();
        aa.printMultiplayer(playerIO);
        playerIO.put("\n                                 [Enter room name]");
        String roomName = "";
        boolean flag = true;
        while (true) {
            flag = true;
            roomName = playerIO.get();
            for (int i = 0; i < hostingPlayersList.size(); ++i) {
                HostingPlayer hostList = (HostingPlayer) hostingPlayersList.get(i);
                if (hostList.getRoomName().equals(roomName)) {
                    playerIO.put("\nA room with that name already exists - please choose another name\n");
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
        playerIO.put("\n                          [Enter amount of total players]");
        int amount = playerIO.getInteger(2, 6);
        HostingPlayer hostingPlayer = new HostingPlayer(playerIO, playerCharacter, roomName, amount);
        hostingPlayersList.add(hostingPlayer);

        playerIO.clear();
        aa.printMultiplayer(playerIO);
        playerIO.put("\n                                    [Your room]\n\n");
        playerIO.put("[Room name] - " + roomName);
        playerIO.put("\n[Players]   - " + amount);
        playerIO.put("\n - Waiting for players to join");

        SyncBox joiningPlayersSB = hostingPlayer.getJoiningPlayersSB();
        ArrayList joiningPlayersList = (ArrayList) joiningPlayersSB.peek();
        System.out.println(joiningPlayersList.size());
        System.out.println(amount);
        while (joiningPlayersList.size() < amount - 1) {
        } //Locking the thread untill someone joins

        ArrayList<ITextIO> playersIOList = new ArrayList();
        playersIOList.add(playerIO);
        ArrayList<DECharacter> playerCharacters = new ArrayList();
        playerCharacters.add(playerCharacter);
        ArrayList<DECharacter> allCharacters = new ArrayList();
        allCharacters.add(playerCharacter);

        for (int i = 0; i < joiningPlayersList.size(); ++i) {
            JoiningPlayer joiningPlayer = (JoiningPlayer) joiningPlayersList.get(i);
            playersIOList.add(joiningPlayer.getPlayerIO());
            playerCharacters.add(joiningPlayer.getPlayerCharacter());
            allCharacters.add(joiningPlayer.getPlayerCharacter());
        }

        NPC enemy = createNPC(playerCharacters);
        allCharacters.add(enemy);
        ArrayList<DECharacter> enemies = new ArrayList();
        enemies.add(enemy);

        ITextIO[] playersIO = new ITextIO[playersIOList.size()];
        for (int i = 0; i < playersIOList.size(); ++i) {
            playersIO[i] = (ITextIO) playersIOList.get(i);
        }

        playerIO.clear();
        aa.printRandom(playerIO);
        List<String> options = Arrays.asList("Short - (3 rooms)", "Medium - (6 rooms)", "Long - (10 rooms)");
        int choiceDifficulty = playerIO.select("\n                                                    [Difficulty]", options, "");

        for (int i = 0; i < hostingPlayersList.size(); ++i) {
            HostingPlayer hostList = (HostingPlayer) hostingPlayersList.get(i);
            if (hostList.getRoomName() == roomName) {
                hostingPlayersList.remove(i);
            }
        }
        switch (choiceDifficulty) {//Player chooses dungeon size (Amount of rooms)
            case 1:
                enterDungeon(playersIO, playerCharacters, 3);
                break;
            case 2:
                enterDungeon(playersIO, playerCharacters, 6);
                break;
            case 3:
                enterDungeon(playersIO, playerCharacters, 10);
                break;
        }

        hostingPlayer.finish();

    }

    private void joinMultiplayerGame(ITextIO playerIO, DECharacter playerCharacter, SyncBox allSyncBoxes) {

        HashMap allSyncBoxesHM = (HashMap) allSyncBoxes.peek();
        SyncBox hostingPlayersListSB = (SyncBox) allSyncBoxesHM.get("hostingPlayersList");
        ArrayList hostingPlayersList = (ArrayList) hostingPlayersListSB.peek();

        playerIO.clear();
        aa.printMultiplayer(playerIO);

        ArrayList<String> options = new ArrayList();
        for (int i = 0; i < hostingPlayersList.size(); ++i) {
            HostingPlayer host = (HostingPlayer) hostingPlayersList.get(i);
            options.add("[Room name] - " + host.getRoomName());
        }
        options.add("Go back");

        int choice = playerIO.select("\n\n                                 [Available Rooms]\n", options, "");

        if (choice != options.size()) {
            HostingPlayer chosenHost = (HostingPlayer) hostingPlayersList.get(choice - 1);
            ArrayList joiningPlayers = (ArrayList) chosenHost.getJoiningPlayersSB().peek();

            playerIO.clear();
            aa.printMultiplayer(playerIO);
            playerIO.put("\n\n                                 [Joined room]\n");
            playerIO.put("W\n - Waiting for players to join");

            JoiningPlayer joiningPlayer = new JoiningPlayer(playerIO, playerCharacter);
            joiningPlayers.add(joiningPlayer);

            while (chosenHost.isGameFinished() == false) {
            } //Locking the thread untill game finishes
        }
    }

    private void singlePlayerDungeon(ITextIO playerIO, DECharacter playerCharacter) {
        playerIO.clear();
        aa.printRandom(playerIO);
        List<String> options = Arrays.asList("Short - (3 rooms)", "Medium - (6 rooms)", "Long - (10 rooms)", "Go back");
        int choiceDifficulty = playerIO.select("\n                                                    [Difficulty]", options, "");
        ITextIO[] playersIO = {playerIO};
        switch (choiceDifficulty) {//Player chooses dungeon size (Amount of rooms)
            case 1:
                enterDungeon(playersIO, Arrays.asList(playerCharacter), 3);
                break;
            case 2:
                enterDungeon(playersIO, Arrays.asList(playerCharacter), 6);
                break;
            case 3:
                enterDungeon(playersIO, Arrays.asList(playerCharacter), 10);
                break;
            case 4:
                break;
        }
    }

    private void showInventory(DECharacter playerCharacter, ITextIO playerIO) {
        playerIO.clear();
        aa.printTreasure(playerIO);
        List actions = Arrays.asList("Show potions", "Show trinkets", "Go back");
        Player p = null;
        try {
            p = pF.getPlayerByID(playerCharacter.getId());
        } catch (CharacterNotFoundException ex) {
            Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
        }
        int choice = playerIO.select("\n\n                       [Inventory]\n[Gold] - " + p.getGold(), actions, "");

        switch (choice) {
            case 1:
                showPotionInventory(playerCharacter, playerIO);
                break;
            case 2:
                showTrinketInventory(playerCharacter, playerIO);
                break;
            case 3:
                break;
        }
    }

    private void showPotionInventory(DECharacter player, ITextIO playerIO) {
        playerIO.clear();
        aa.printPotion(playerIO);
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

        actions.add("Go back");
        //Gets player input
        int choice = playerIO.select("\n\n                            [Potion inventory]\n", actions, "");
        //Gets selected potion from the database.
        System.out.println(actions.size());
        if (choice != actions.size()) {
            Potion chosen = null;
            try {
                chosen = pfc.getPotionByID(potionIds.get(choice - 1));
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
            Player currentPlayer = null;
            try {
                currentPlayer = pF.getPlayerByID(player.getId());
            } catch (CharacterNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Consumes potion and removes it from the players inventory.
            pfc.usePotion(player, chosen);
            playerIO.put("\nYou have consumed " + chosen.getName() + "\nhp: " + currentPlayer.getHealth()
                    + "dmg: " + currentPlayer.getAttackDmg() + "\n");
            ifc.removeFromInventory(player, choice - 1);
        }
    }

    private void showTrinketInventory(DECharacter player, ITextIO playerIO) {
        playerIO.clear();
        aa.printTrinket(playerIO);
        ArrayList<String> actions = new ArrayList();
        List<Long> trinketIds = new ArrayList();

        Inventory inv = ifc.getInventory(player, player.getInventory().getId());
        trinketIds = inv.getTrinketIds();

        for (Long longs : trinketIds) {
            try {
                actions.add(tfc.getTrinketById(longs).getName() + " - " + tfc.getTrinketById(longs).getInfo());
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        actions.add("Go back");

        int choice = playerIO.select("\n\n                              [Trinket inventory]\n", actions, "");

        ArrayList<String> use = new ArrayList();
        use.add("Equip");
        use.add("Drop");
        use.add("Return to menu");

        //Gets selected potion from the database.
        System.out.println(actions.size());
        if (choice != actions.size()) {
            Trinket chosen = null;
            try {
                chosen = tfc.getTrinketById(trinketIds.get(choice - 1));
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }

            int useChoice = playerIO.select("What will you do?", use, "");
            if (useChoice != use.size()) {
                switch (useChoice) {
                    case 1:
                        tfc.equipTrinket(player, chosen);
                        break;
                    case 2:
                        ifc.removeTrinketFromInventory(player, choice - 1);
                        break;
                    case 3:
                        break;
                }

            }

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

    private void encounter(ITextIO[] playersIO, List<DECharacter> allCharacters, List<DECharacter> team1, List<DECharacter> team2, boolean pve) {
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
                            i = allCharacters.size();
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
                            i = allCharacters.size();
                            for (int j = 0; j < playersIO.length; j++) {
                                playersIO[j].put("You killed the enemy! \n");
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
        playerIO.put("[" + npc.getName() + "] - Stats\n");
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
            actions.add(ab.getName() + "\n" + "  - " + ab.getDescription() + "\n");
        }
        int choice = players[i].select("\n[Action menu]", actions, "");

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
        int targetIndex = players[i].select("\n[Choose your target]", names, "");
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
    }

    private void enterDungeon(ITextIO[] playersIO, List<DECharacter> allCharacters, int amountOfRooms) {

        printDungeonLocation(playersIO);

        for (int i = 0; i < amountOfRooms; ++i) {
            enterRoom(playersIO, allCharacters);
            if (i != amountOfRooms - 1) {
                for (ITextIO playerIO : playersIO) {
                    playerIO.put("Press enter to move to the next room ..");
                    playerIO.get();
                }
            } else {
                for (ITextIO playerIO : playersIO) {
                    pF.updatePlayer(allCharacters.get(i));
                    playerIO.put("You finished the dungeon! Press enter to return ..");
                    playerIO.get();
                }
            }
        }
    }

    private void printDungeonLocation(ITextIO[] playersIO) {
        AsciiArt aa = new AsciiArt();
        String[] locations = {"a cave", "an abandoned castle", "an old haunted farm house", "a sewer", "a razed city", "a forgotten bunker"};
        int rand = (int) (Math.random() * 4);
        String location = locations[rand];
        Faker faker = new Faker();
        String region = faker.elderScrolls().region();
        for (int i = 0; i < playersIO.length; ++i) {
            playersIO[i].clear();
            aa.printDungeon(playersIO[i]);
            playersIO[i].put("\n\nYou found " + location + " somwhere in " + region + ".\n\n");
            playersIO[i].put("Press enter to enter the dungeon..");
            playersIO[i].get();
        }
    }

    private void enterRoom(ITextIO[] playersIO, List<DECharacter> players) {
        //Random generates a number of either 0 and 1
        int rand = (int) (Math.random() * 3);

        //66% chance of encounter
        if (rand > 0) {
            //Creates an enemy based on the players
            NPC enemy = createNPC(players);

            //Puts all character in their correct list
            List<DECharacter> allCharacters = new ArrayList();
            List<DECharacter> enemies = new ArrayList();
            for (int j = 0; j < playersIO.length; j++) {
                allCharacters.add(players.get(j));
            }
            allCharacters.add(enemy);
            enemies.add(enemy);

            //Encounter
            encounter(playersIO, allCharacters, players, enemies, true);

            //Variables for gold and xp rewards
            int xpGain = 0;
            int avrgLevel = 0;
            int avrgRequiredXp = 0;

            for (DECharacter p : players) {
                Player character = null;
                try {
                    character = pF.getPlayerByID(p.getId());
                } catch (CharacterNotFoundException ex) {
                    Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
                }
                avrgLevel += p.getLevel();
                avrgRequiredXp += character.getNeededExp();
            }
            avrgLevel = avrgLevel / players.size();
            avrgRequiredXp = avrgRequiredXp / players.size();
            int goldDrop = (int) ((Math.random() * 10) * avrgLevel);
            if (avrgLevel == 1) {
                xpGain += Math.pow(avrgLevel + 1, (avrgRequiredXp / (avrgRequiredXp / 3)));
            } else {
                xpGain += avrgLevel * Math.pow(avrgLevel, 3);
            }

            for (DECharacter p : players) {
                pF.recieveExperience(p.getId(), xpGain);
                pF.lootGold(p, goldDrop);
            }

            for (int i = 0; i < players.size(); i++) {
                playersIO[i].put("You have gained " + xpGain + " XP and " + goldDrop + " Gold.\n");
            }

        } else {
            for (int i = 0; i < players.size(); i++) {
                playersIO[i].clear();
                playersIO[i].put("No enemies in sight, it's safe to move on to the next room.\n");
            }
        }

        for (int i = 0; i < players.size(); ++i) {
            rand = (int) (Math.random() * 3);
            //33% chance of reward
            if (rand == 0) {
                rewardPlayer(playersIO[i], players.get(i));
            }
        }
    }

    private void rewardPlayer(ITextIO playerIO, DECharacter player) {
        //Rewards should be a new method
        //Determines the amount of potions the player gets as a reward
        int amountOfPotions = (int) (Math.random() * 3) + 1;
        playerIO.put("\nYou found some items!\n");
        //Adds random potions with the amount equal to the random number above
        List<Long> potionIDs = new ArrayList();
        for (int i = 0; i < amountOfPotions; ++i) {
            double potionID = (Math.random() * 3) + 1;
            try {
                playerIO.put("\nYou found a " + pfc.getPotionByID((long) potionID).getName() + "\n");
            } catch (ItemNotFoundException ex) {
                //Couldnt find the item exception
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
            potionIDs.add((long) potionID);
        }

        //Adds single random trinket
        List<Long> trinketIds = new ArrayList();
        int trinketChance = (int) (Math.random() * 10);
        if (trinketChance > 6) {
            double trinketID = (Math.random() * 3) + 1;
            try {
                playerIO.put("You found a " + tfc.getTrinketById((long) trinketID).getName() + "\n");
            } catch (ItemNotFoundException ex) {
                //Couldnt find the item exception
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
            trinketIds.add((long) trinketID);
        }
        //Creates a new inventory with the potions 
        Inventory inventory = new Inventory(potionIDs, trinketIds);
        playerIO.put("\n");
        //Adds the items from the new inventory to the existing inventory of the player
        ifc.addToInventory(player, inventory);
    }

}
