/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.HelpingClasses;

import DarkestEnemies.IF.DECharacter;
import DarkestEnemies.syncbox.SyncBox;
import DarkestEnemies.textio.ITextIO;
import java.util.ArrayList;

/**
 *
 * @author Gamer
 */
public class HostingPlayer {
    
    private ITextIO playerIO;
    private DECharacter playerCharacter;
    private String roomName;
    private SyncBox joiningPlayersSB;
    private int maxAmount;
    private boolean gameFinished;

    public HostingPlayer(ITextIO playerIO, DECharacter playerCharacter, String roomName, int maxAmount) {
        this.playerIO = playerIO;
        this.playerCharacter = playerCharacter;
        this.roomName = roomName;
        this.joiningPlayersSB = new SyncBox();
        ArrayList joiningPlayers = new ArrayList<JoiningPlayer>();
        this.joiningPlayersSB.put(joiningPlayers);
        this.maxAmount = maxAmount;
        this.gameFinished = false;
    }

    public ITextIO getPlayerIO() {
        return playerIO;
    }

    public DECharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public String getRoomName() {
        return roomName;
    }    

    public SyncBox getJoiningPlayersSB() {
        return joiningPlayersSB;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void finish() {
        this.gameFinished = true;
    }
    
    
}
