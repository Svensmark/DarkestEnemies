/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.facades;

import DarkestEnemies.Entity.Account;
import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.exceptions.AccountNotFoundException;
import entities.exceptions.WrongPasswordException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Gamer
 */
public class AccountFacade {

    private static AccountFacade instance;
    private static EntityManagerFactory emf;
    private static int workload = 12;

    private AccountFacade() {
    }

    public static AccountFacade getReviewFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AccountFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void createAccount(String username, String password) {
        EntityManager em = getEntityManager();
        try {
            Account account = new Account(username, hashPassword(password));
            em.getTransaction().begin();
            em.persist(account);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public DECharacter login(String username, String password) throws AccountNotFoundException, WrongPasswordException{        
        Query query = getEntityManager().createQuery("SELECT new account FROM Account account WHERE account.username = :username", Account.class);
        Account account = (Account) query.setParameter("username", username).getResultList().get(0);     
        //The account was not found
        if (account == null) {
            throw new AccountNotFoundException("Something went wrong, the account returned null");
        }
        
        //If the password is correct
        if(checkPassword(password, account.getPassword())) {
            return account.getCharacter();
        } else {
            throw new WrongPasswordException("The password does not match the usename, check if either is correct");
        }
    }

    /**
     * This method can be used to generate a string representing an account
     * password suitable for storing in a database. It will be an OpenBSD-style
     * crypt(3) formatted hash string of length=60 The bcrypt workload is
     * specified in the above static variable, a value from 10 to 31. A workload
     * of 12 is a very reasonable safe default as of 2013. This automatically
     * handles secure 128-bit salt generation and storage within the hash.
     *
     * @param password_plaintext The account's plaintext password as provided
     * during account creation, or when changing an account's password.
     * @return String - a string of length 60 that is the bcrypt hashed password
     * in crypt(3) format.
     */
    private static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);

        return (hashed_password);
    }

    /**
     * This method can be used to verify a computed hash from a plaintext (e.g.
     * during a login request) with that of a stored hash from a database. The
     * password hash from the database must be passed as the second variable.
     *
     * @param password_plaintext The account's plaintext password, as provided
     * during a login request
     * @param stored_hash The account's stored password hash, retrieved from the
     * authorization database
     * @return boolean - true if the password matches the password of the stored
     * hash, false otherwise
     */
    private static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if (null == stored_hash || !stored_hash.startsWith("$2a$")) {
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        }

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return (password_verified);
    }
}
