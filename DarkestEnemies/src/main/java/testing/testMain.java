/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import DarkestEnemies.Entity.Ability;
import DarkestEnemies.Entity.Player;
import DarkestEnemies.facades.AbilityFacade;
import DarkestEnemies.facades.AccountFacade;
import DarkestEnemies.facades.PlayerFacade;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

/**
 *
 * @author Gamer
 */
public class testMain {

    public static void main(String[] args) throws Exception {

        EntityManagerFactory _emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);

        //Setups an account and a character to that account:
        AccountFacade acF = AccountFacade.getAccountFacade(_emf);
        PlayerFacade pF = PlayerFacade.getPlayerFacade(_emf);
        AbilityFacade abF = AbilityFacade.getAbilityFacade(_emf);
        
        Ability ability1 = new Ability(15, 0, 1, 1, "Fuck", "Fucks the target very hard");
        Ability ability2 = new Ability(1000, 0, 1, 1, "Execute", "Executes the target");
        abF.persistAbility(ability1);
        abF.persistAbility(ability2);

        List<Ability> abilities = new ArrayList();        
        abilities.add(ability1);
        
        
        Player player1 = new Player("Name", 10000, 10000, 10000, 10000, abilities);        
        pF.persistPlayer(player1);
        
        
        /*
//        
        Player cha = (Player) acF.login("Svense1", "test");
        System.out.println(cha.getName());
        Ability abi = abF.getAbilityByName("Execute");
        System.out.println(abi.getName());

        pF.addAbilityToPlayer(cha.getId(), abi);*/

    }

}
