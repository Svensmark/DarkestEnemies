package DarkestEnemies.Server;

import DarkestEnemies.textio.ITextIO;

/**
 *
 * @author asgerhs
 */
public class testGame implements ITextGame{

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public void startGame(ITextIO[] players) {
        int secret = 50;
        String[] names = new String[players.length];
        for(int i = 0; i < players.length; ++i)
        {
            players[i].put("Welcome to the NotSoCoolGame!\n");
            players[i].put("Please enter your name:\n");
            names[i] = players[i].get();
            for(int j = 0; j < players.length; ++j)
            {
                players[j].put("Player " + (i+1) + " is " + names[i] + "\n");
            }
        }
        
        for(int i = 0; i < players.length; ++i)
        {
            players[i].put("Game started!\n");
        }
        
        boolean gameOver = false;
        while(!gameOver)
        {
            for(int i = 0; i < players.length; ++i)
            {
                players[i].put("Make guess...\n");
                int guess = players[i].getInteger(1, 100);
                for(int j = 0; j < players.length; ++j)
                {
                    players[j].put("Player " + (i+1) + ": " + names[i] + " guessed on " + guess + "\n");
                }
                if(guess == secret)
                {
                    gameOver = true;
                    for(int j = 0; j < players.length; ++j)
                    {
                        if(i == j)
                        {
                            players[j].put("Game over, you won the game.\n");
                        }
                        else
                        {
                            players[j].put("Game over, player " + (i+1) + ": " + names[i] + " won the game.\n");
                        }
                    }
                }
            }
        }
    }

    
    
    public static void main(String[] args) {
        int port = 3737;
        GameServer server = new GameServer(new testGame(), port);
        server.run();
    }
    
} 
