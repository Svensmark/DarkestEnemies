/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.Server.AsciiArt;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.PlayerNotFoundException;
import DarkestEnemies.textio.ITextIO;
import java.util.List;
import DarkestEnemies.exceptions.CharacterNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 *
 * @author Gamer
 */
public class PlayerFacade {

    private static PlayerFacade instance;
    private static EntityManagerFactory emf;

    public static PlayerFacade getPlayerFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PlayerFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void updatePlayer(DECharacter player) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Player p = em.find(Player.class, player.getId());
            p.setHealth(player.getHealth());
            p.setAttackDmg(player.getMaxAttackDmg());
            em.merge(p);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public void lootGold(DECharacter player, int gold){
        EntityManager em = getEntityManager();
        try{
            em.getTransaction().begin();
            Player p = em.find(Player.class, player.getId());
            p.addGold(gold);
            em.merge(p);
            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }

    public void persistPlayer(Player player) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(player);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Player createNewPlayer(ITextIO user) {
        AccountFacade af = AccountFacade.getAccountFacade(emf);
        user.clear();
        AsciiArt aa = new AsciiArt();
        aa.printTitle(user);
        user.put("\n\n                                                        [Create account]\n\n");
        user.put("New username:");
        String username = user.get();
        user.put("New password:");
        String password = user.get();
        Account a = af.createAccount(username, password);

        user.put("Character name:");
        String playerName = user.get();
        Player player = new Player(playerName);

        af.addCharacterToAccount(a, player);
        user.put("\nSucces - you can now login with your account\n");
        user.put("Press enter to return ..");
        user.get();
        user.clear();
        return player;
    }

    public Player getPlayerByName(String name) throws PlayerNotFoundException {
        Query query = getEntityManager().createQuery("SELECT player FROM Player player WHERE player.name = :name", Player.class);
        List<Player> players = query.setParameter("name", name).getResultList();
        //The account was not found
        if (players.isEmpty()) {
            throw new PlayerNotFoundException("Something went wrong, the result was returned null\n");
        }
        return players.get(0);
    }

    public Player getPlayerByID(Long id) throws CharacterNotFoundException {
        EntityManager em = getEntityManager();
        Player player = em.find(Player.class, id);
        if (player == null) {
            throw new CharacterNotFoundException("Character with that ID was not found.");
        }
        return player;
    }

    public void addAbilityToPlayer(Long playerID, Ability ability) {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            Player foundPlayer = em.find(Player.class, playerID);
            Ability foundAbility = em.find(Ability.class, ability.getId());

            foundPlayer.addAbility(foundAbility);

            em.merge(foundPlayer);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public void recieveExperience(long id, int xp){
        EntityManager em = getEntityManager();
        int totalXp;
        try{
            em.getTransaction().begin();
            Player p = em.find(Player.class, id);
            if((p.getCurrentExp() + xp) > p.getNeededExp()){
                totalXp = (p.getCurrentExp() + xp) - p.getNeededExp();
                levelUp(id);
                p.setCurrentExp(totalXp);
                em.merge(p);
            }else{
            p.setCurrentExp(p.getCurrentExp() + xp);
            }
            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }
    
    private void levelUp(long id){
        EntityManager em = getEntityManager();
        try{
            em.getTransaction().begin();
            Player p = em.find(Player.class, id);
            p.setLevel(p.getLevel() + 1);
            p.levelUp();
            em.merge(p);
            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }

 

    public void removeGoldFromPlayer(Player player, int amount) {
        player.removeGold(amount);
        updatePlayer(player);
    }

}
