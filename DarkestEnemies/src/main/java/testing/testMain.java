/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Account;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.PlayerFacade;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

/**
 *
 * @author Gamer
 */
public class testMain {
    
    public static void main(String[] args) {
        
        EntityManagerFactory _emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
        
        //Setups an account and a character to that account:
        AccountFacade acF = AccountFacade.getAccountFacade(_emf);
        PlayerFacade pF = PlayerFacade.getPlayerFacade(_emf);
         AbilityFacade abF = AbilityFacade.getAbilityFacade(_emf);
        
        Player player1 = new Player("New player1");        
        Account acc1 = acF.createAccount("Svense1", "test");        
        acF.addCharacterToAccount(acc1, player1);
        
        Ability ability1 = new Ability(15, 0, 1, 1, "Fuck", "Fucks the target very hard");
        Ability ability2 = new Ability(1000, 0, 1, 1, "Execute", "Executes the target");
        abF.persistAbility(ability1);
        abF.persistAbility(ability2);
        
        //Setups an account and a character to that account:
        Player player2 = new Player("New player2");        
        Account acc2 = acF.createAccount("Svense2", "test");        
        acF.addCharacterToAccount(acc2, player2);
        
       
        
        pF.addAbilityToPlayer(player1, ability1);
        pF.addAbilityToPlayer(player1, ability2);
        pF.addAbilityToPlayer(player2, ability1);
        pF.addAbilityToPlayer(player2, ability2);
        
    }
    
}
