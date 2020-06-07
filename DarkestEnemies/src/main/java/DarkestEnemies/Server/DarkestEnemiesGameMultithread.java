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
import DarkestEnemies.HelpingClasses.HostingPlayer;
import DarkestEnemies.HelpingClasses.JoiningPlayer;
import com.github.javafaker.Faker;
import entities.exceptions.WrongPasswordException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    boolean equipped = false;

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
            List<String> options = Arrays.asList("Find dungeon", "Vendor", "Inventory", "Character", "Log out");
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

                case 2:
                    showVendor(playerCharacter, playerIO);
                    break;
                //Player chooses inventory
                case 3:
                    showInventory(playerCharacter, playerIO);
                    break;

                //Player chooses inventory
                case 4:
                    showCharacter(playerCharacter, playerIO);
                    break;
                //Player logs out
                case 5: {
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
        while (joiningPlayersList.size() < (amount - 1)) {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println(joiningPlayersList.size());
            } catch (InterruptedException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            playerIO.put("\n - Waiting for players ..");

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

    private void showVendor(DECharacter playerCharacter, ITextIO playerIO) {
        while (true) {
            List<Potion> allPotions = pfc.getAllPotions();
            List<Trinket> allTrinkets = tfc.getAllTrinkets();

            ArrayList<String> potionActions = new ArrayList();
            ArrayList<String> trinketActions = new ArrayList();
            List actions = Arrays.asList("Potions", "Trinkets", "Go Back");

            for (Potion potions : allPotions) {
                potionActions.add(potions.getName() + " - " + potions.getInfo());
            }
            potionActions.add("Go back");

            for (Trinket trinkets : allTrinkets) {
                trinketActions.add(trinkets.getName() + " - " + trinkets.getInfo());
            }
            trinketActions.add("Go back");

            int choice = playerIO.select("\n\n                            [Vendor]\n", actions, "");
            if (choice != actions.size()) {
                switch (choice) {
                    case 1:
                        showVendorPotions(playerCharacter, playerIO);
                        break;
                    case 2:
                        showVendorTrinkets(playerCharacter, playerIO);
                        break;
                    case 3:
                        break;
                }
            } else {
                break;
            }

        }

    }

    private void showVendorPotions(DECharacter playerCharacter, ITextIO playerIO) {

        while (true) {
            try {

                List<Potion> allPotions = pfc.getAllPotions();

                ArrayList<String> actions = new ArrayList();

                Player player = pF.getPlayerByID(playerCharacter.getId());

                List<Long> trinketIds = new ArrayList();
                List<Long> equippedTrinketIds = new ArrayList();
                List<Long> potionId = new ArrayList();
                Inventory newInventory = new Inventory(potionId, trinketIds, equippedTrinketIds);

                for (Potion potions : allPotions) {
                    actions.add(potions.getName() + " - " + potions.getInfo() + " [" + potions.getBuyValue() + " gold]");
                }
                actions.add("Go back");

                int choice = playerIO.select("\n\n                            [Potions]\n", actions, "");

                if (choice != actions.size()) {
                    long chosen = allPotions.get(choice - 1).getId();
                    if (player.getGold() < allPotions.get(choice - 1).getBuyValue()) {
                        playerIO.clear();
                        playerIO.put("You don't have enough gold to purchase that.");
                    } else {
                        potionId.add(chosen);
                        newInventory.setPotionIds(potionId);
                        ifc.addToInventory(playerCharacter, newInventory);
                        player.removeGold(allPotions.get(choice - 1).getBuyValue());
                        playerIO.clear();
                        playerIO.put("YOu have purchased " + allPotions.get(choice - 1).getName());
                    }
                } else {
                    break;
                }
            } catch (CharacterNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
                playerIO.put("Something went wrong when trying to find your character, please try again.");
            }

        }

    }

    private void showVendorTrinkets(DECharacter playerCharacter, ITextIO playerIO) {
        while (true) {
            try {

                List<Trinket> allTrinkets = tfc.getAllTrinkets();

                ArrayList<String> actions = new ArrayList();

                Player player = pF.getPlayerByID(playerCharacter.getId());

                List<Long> trinketIds = new ArrayList();
                List<Long> equippedTrinketIds = new ArrayList();
                List<Long> potionId = new ArrayList();
                Inventory newInventory = new Inventory(potionId, trinketIds, equippedTrinketIds);

                for (Trinket trinket : allTrinkets) {
                    actions.add(trinket.getName() + " - " + trinket.getInfo() + " [" + trinket.getBuyValue() + " gold]");
                }
                actions.add("Go back");

                int choice = playerIO.select("\n\n                            [Trinkets]\n", actions, "");

                if (choice != actions.size()) {
                    long chosen = allTrinkets.get(choice - 1).getId();
                    if (player.getGold() < allTrinkets.get(choice - 1).getBuyValue()) {
                        playerIO.clear();
                        playerIO.put("You don't have enough gold to purchase that.");
                    } else {
                        trinketIds.add(chosen);
                        newInventory.setTrinketIds(trinketIds);
                        ifc.addToInventory(playerCharacter, newInventory);
                        player.removeGold(allTrinkets.get(choice - 1).getBuyValue());
                        playerIO.clear();
                        playerIO.put("You have purchased " + allTrinkets.get(choice - 1).getName());
                    }
                } else {
                    break;
                }
            } catch (CharacterNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
                playerIO.put("Something went wrong when trying to find your character, please try again.");
            }

        }
    }

    private void showInventory(DECharacter playerCharacter, ITextIO playerIO) {
        while (true) {
            playerIO.clear();
            aa.printTreasure(playerIO);
            List actions = Arrays.asList("Show potions", "Show equipped trinkets", "Show unequipped trinkets", "Go back");
            Player p = null;
            try {
                p = pF.getPlayerByID(playerCharacter.getId());
            } catch (CharacterNotFoundException ex) {
                Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
            }
            int choice = playerIO.select("\n\n                       [Inventory]\n[Gold] - " + p.getGold(), actions, "");
            if (choice != actions.size()) {
                switch (choice) {
                    case 1:
                        showPotionInventory(playerCharacter, playerIO);
                        break;
                    case 2:
                        showEquippedTrinketInventory(playerCharacter, playerIO);
                        break;
                    case 3:
                        showUnequippedTrinketInventory(playerCharacter, playerIO);
                        break;
                    case 4:
                        break;
                }
            } else {
                break;
            }

        }
    }

    private void showPotionInventory(DECharacter player, ITextIO playerIO) {
        while (true) {
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
            } else {
                break;
            }
        }
    }

    private void showUnequippedTrinketInventory(DECharacter player, ITextIO playerIO) {
        while (true) {
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
                            ifc.equipTrinket(player, chosen.getId().intValue());
                            ifc.removeTrinketFromInventory(player, choice - 1);
                            break;
                        case 2:
                            ifc.removeTrinketFromInventory(player, choice - 1);
                            break;
                        case 3:
                            break;
                    }

                }

            } else {
                break;
            }
        }
    }

    private void showEquippedTrinketInventory(DECharacter player, ITextIO playerIO) {
        while (true) {
            playerIO.clear();
            aa.printTrinket(playerIO);
            ArrayList<String> actions = new ArrayList();
            List<Long> trinketIds = new ArrayList();

            Inventory inv = ifc.getInventory(player, player.getInventory().getId());
            trinketIds = inv.getEquippedTrinketIds();

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
            use.add("Unequip");
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
                            tfc.unequipTrinket(player, chosen);
                            ifc.unequipTrinket(player, chosen.getId().intValue());
                            ifc.removeTrinketFromEquippedInventory(player, choice - 1);
                            break;
                        case 2:
                            ifc.removeTrinketFromEquippedInventory(player, choice - 1);
                            break;
                        case 3:
                            break;
                    }

                }

            }else{
                break;
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

    private boolean encounter(ITextIO[] playersIO, List<DECharacter> allCharacters, List<DECharacter> team1, List<DECharacter> team2, boolean pve) {
        //Setups bools
        boolean team1Alive = true; //Always players when PVE - Always players when PVP
        boolean team2Alive = true; //Always hostile NPC when PVE - Always players when PVP

        int random = 1 + (int) (Math.random() * 4);

        //While both teams are alive the encounter is ongoing (keeps looping)
        while (team1Alive == true && team2Alive == true) {
            //Each character has a turn
            for (int i = 0; i < allCharacters.size(); i++) {

                //If the the character is an NPC                               
                if (allCharacters.get(i).getClass() == NPC.class) {
                    npcAction(playersIO, team1, (NPC) allCharacters.get(i), random);
                    for (int j = 0; j < team1.size(); ++j) {
                        visualCandy1(playersIO[j], team2, random);
                    }
                }

                //If the character is a player
                if (allCharacters.get(i).getClass() != NPC.class) {
                    for (DECharacter npc : team2) {
                        printNPCStats(playersIO[i], npc, random);
                    }
                    playerAction(playersIO[i], allCharacters.get(i), team1, team2, random);
                }

                //Checks HP for all team1 members
                for (DECharacter team1character : team1) {
                    //Enters if-statement if a member of team 1 is dead
                    if (team1character.getHealth() <= 0) {
                        //Removes the dead member

                        for (int j = 0; j < playersIO.length; j++) {
                            playersIO[j].put("A teammember has been killed - the party runs away!\n"
                                    + "You will be returned to the menu!");
                            return true;
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
        return false;
    }

    private void printNPCStats(ITextIO playerIO, DECharacter npc, int i) {
        playerIO.clear();
        aa.printSpecifc(playerIO, i);
        playerIO.put("\n---------------------------------------------------------------------------------------------------\n\n");
        playerIO.put("[" + npc.getCharacterName() + "] - Stats\n");
        playerIO.put("- " + npc.getHealth() + " HP \n");
        playerIO.put("- " + npc.getAttackDmg() + " ATK \n\n");
        playerIO.put("---------------------------------------------------------------------------------------------------\n\n");
    }

    private void npcAction(ITextIO[] playersIO, List<DECharacter> opposingTeam, NPC npc, int random) {
        for (int j = 0; j < playersIO.length; j++) {
            visualCandy1(playersIO[j], Arrays.asList(npc), random);
            opposingTeam.get(j).setHealth(opposingTeam.get(j).getHealth() - npc.getAttackDmg());
            playersIO[j].put(npc.getCharacterName() + " has hit you for " + npc.getAttackDmg() + ". \nYou now have " + opposingTeam.get(j).getHealth() + " hp left!\n\n");
            playersIO[j].put("Press enter to continue");
            playersIO[j].get();
        }
    }

    private void playerAction(ITextIO playerIO, DECharacter playerCharacter, List<DECharacter> allies, List<DECharacter> enemies, int random) {
        //printNPCStats(playerIO, randomPrintID);

        //Creates an empty list of available actions
        ArrayList<String> actions = new ArrayList();

        //Gets the abilities of the current player
        List<Ability> abilities = playerCharacter.getAbilities();

        //Adds the abils name to actions list so it can be selected
        for (Ability ab : abilities) {
            actions.add(ab.getName() + "\n" + "  - " + ab.getDescription() + "\n");
        }
        int choice = playerIO.select("[Action menu]", actions, "");

        //Gets the chosen ability
        Ability chosenAbility = playerCharacter.getAbilities().get(choice - 1);

        //Creates a new list of all the names in the encounter
        ArrayList<String> names = new ArrayList();
        ArrayList<DECharacter> availableTargets = new ArrayList();

        //Only adds the valid targets to the list of names
        //If the chosen ability is a healing ability adds names from the enemy team
        if (chosenAbility.getDamage() <= 0 && chosenAbility.getHealing() > 0) {
            for (int j = 0; j < allies.size(); j++) {
                names.add(allies.get(j).getCharacterName());
                availableTargets.add(allies.get(j));
            }
            //Else if the chosen abilty is a damage ability adds names from the ally team
        } else if (chosenAbility.getHealing() <= 0 && chosenAbility.getDamage() > 0) {
            for (int i = 0; i < enemies.size(); i++) {
                names.add(enemies.get(i).getCharacterName());
                availableTargets.add(enemies.get(i));
            }
        }

        //Visual candy
        visualCandy1(playerIO, enemies, random);

        //Creates a new list of all the chosen targets of the ability
        ArrayList<Integer> targetsIndex = new ArrayList();
        int targetIndex = playerIO.select("\n[Choose your target]", names, "");
        for (int j = 0; j < playerCharacter.getAbilities().get(choice - 1).getAmountOfTargets(); ++j) {
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
                if (target.getHealth() > target.getMaxHealth()) {
                    target.setHealth(target.getMaxHealth());
                }
                visualCandy1(playerIO, enemies, random);
                playerIO.put("Hit! " + target.getCharacterName() + " now has " + target.getHealth() + " HP left!\n\n");
            } else if (chosenAbility.getHealing() <= 0 && chosenAbility.getDamage() > 0) {
                target.setHealth(target.getHealth() - (chosenAbility.getDamage() + playerCharacter.getAttackDmg()));
                visualCandy1(playerIO, enemies, random);
                playerIO.put("Hit! " + target.getCharacterName() + " now has " + target.getHealth() + " HP left!\n\n");
            }
        }

        playerIO.put("Press enter to continue");
        playerIO.get();
    }

    private void visualCandy1(ITextIO playerIO, List<DECharacter> enemies, int random) {
        //Visual candy
        playerIO.clear();
        for (DECharacter npc : enemies) {
            printNPCStats(playerIO, npc, random);
        }
    }

    private void enterDungeon(ITextIO[] playersIO, List<DECharacter> allCharacters, int amountOfRooms) {

        printDungeonLocation(playersIO);

        for (int i = 0; i < amountOfRooms; ++i) {
            boolean deadTeammate = enterRoom(playersIO, allCharacters);
            if (i != amountOfRooms - 1) {
                for (ITextIO playerIO : playersIO) {
                    playerIO.put("Press enter to move to the next room ..");
                    playerIO.get();
                }
            } else {
                for (int j = 0; j < playersIO.length; ++j) {
                    playersIO[j].put("You finished the dungeon! Press enter to return ..");
                    playersIO[j].get();
                }
            }

            if (deadTeammate) {
                break;
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

    private boolean enterRoom(ITextIO[] playersIO, List<DECharacter> players) {
        //Random generates a number of either 0 and 1
        int rand = (int) (Math.random() * 3);
        boolean deadTeammate = false;

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
            deadTeammate = encounter(playersIO, allCharacters, players, enemies, true);
            //Reward from encounter
            if (!deadTeammate) {
                for (int i = 0; i < playersIO.length; ++i) {
                    rewardPlayer(playersIO[i], players.get(i));
                }
            }

        } else {
            for (int i = 0; i < players.size(); i++) {
                playersIO[i].clear();
                aa.printEmptyRoom(playersIO[i]);
                playersIO[i].put("\n--------------------------------------------------------------------------------------------------");
                playersIO[i].put("\n\nNo enemies in sight, it's safe to move on to the next room.\n");
                playersIO[i].put("\n--------------------------------------------------------------------------------------------------\n\n");
            }
        }
        return deadTeammate;
    }

    private void rewardPlayer(ITextIO playerIO, DECharacter playerDE) {

        Player player = (Player) playerDE;

        //Variables for gold and xp rewards
        int xpGain = 0;
        int avrgLevel = player.getLevel();
        int avrgRequiredXp = player.getNeededExp();

        int goldDrop = (int) ((Math.random() * 10) * avrgLevel);
        if (avrgLevel == 1) {
            xpGain += Math.pow(avrgLevel + 1, (avrgRequiredXp / (avrgRequiredXp / 3)));
        } else {
            xpGain += avrgLevel * Math.pow(avrgLevel, 3);
        }

        //Basic reward - gold and exp
        pF.recieveExperience(player.getId(), xpGain);
        pF.lootGold(player, goldDrop);

        playerIO.put("You have gained " + xpGain + " XP and " + goldDrop + " Gold.\n");

        //33% chance of loot
        int rand = (int) (Math.random() * 3);
        if (rand == 0) {
            //Determines the amount of potions the player gets as a reward
            int amountOfPotions = (int) (Math.random() * 3) + 1;
            playerIO.put("\nYou found some items!\n");
            //Adds random potions with the amount equal to the random number above
            List<Long> potionIDs = new ArrayList();
            for (int i = 0; i < amountOfPotions; ++i) {
                double potionID = (Math.random() * 3) + 1;
                try {
                    playerIO.put("You found a " + pfc.getPotionByID((long) potionID).getName() + "\n");
                } catch (ItemNotFoundException ex) {
                    //Couldnt find the item exception
                    Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
                }
                potionIDs.add((long) potionID);
            }

            //Adds single random trinket
            List<Long> trinketIds = new ArrayList();
            List<Long> equippedTrinketIds = new ArrayList();
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
            Inventory inventory = new Inventory(potionIDs, trinketIds, equippedTrinketIds);
            playerIO.put("\n");
            //Adds the items from the new inventory to the existing inventory of the player
            ifc.addToInventory(player, inventory);
        }

        pF.updatePlayer(player);

    }

    private void showCharacter(DECharacter playerCharacterDE, ITextIO playerIO) {
        try {
            Player playerCharacter = pF.getPlayerByID(playerCharacterDE.getId());
            playerIO.clear();
            aa.printCharacter(playerIO);
            playerIO.put("\n\n                                      [Character]\n\n");
            playerIO.put(">------------------------------------\n");
            playerIO.put("[Character name]  - " + playerCharacter.getCharacterName() + "\n");
            playerIO.put("[Level]           - " + playerCharacter.getLevel() + "\n");
            playerIO.put("[Exp Points]      - (" + playerCharacter.getCurrentExp() + " / " + playerCharacter.getNeededExp() + ")\n\n");
            playerIO.put("[Health Points]   - (" + playerCharacter.getHealth() + " / " + playerCharacter.getMaxHealth() + ")\n");
            playerIO.put("[Mana Points]     - (" + playerCharacter.getMana() + " / " + playerCharacter.getMaxMana() + ")\n\n");
            playerIO.put("[Atk Dmg]         - " + playerCharacter.getAttackDmg() + "\n");
            playerIO.put(">------------------------------------");
            List choices = Arrays.asList("Go back");
            playerIO.select("", choices, "");
        } catch (CharacterNotFoundException ex) {
            Logger.getLogger(DarkestEnemiesGameMultithread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
