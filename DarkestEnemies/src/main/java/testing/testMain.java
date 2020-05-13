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
import com.github.javafaker.Faker;
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

        int rand = (int) (Math.random() * 3);
        System.out.println(rand);
        rand = (int) (Math.random() * 3);
        System.out.println(rand);
        rand = (int) (Math.random() * 3);
        System.out.println(rand);
        rand = (int) (Math.random() * 3);
        System.out.println(rand);

    }

}
