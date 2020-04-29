package DarkestEnemies.Server;

import DarkestEnemies.textio.ITextIO;

/**
 *
 * @author asgerhs
 */
public interface ITextGame {

    public int getNumberOfPlayers();

    public void startGame(ITextIO[] players);

}
