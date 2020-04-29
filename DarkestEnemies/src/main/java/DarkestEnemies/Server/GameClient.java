package DarkestEnemies.Server;

import DarkestEnemies.textio.ITextIO;
import DarkestEnemies.textio.SysTextIO;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author asgerhs
 */
public class GameClient implements Runnable{

    private final String host;
    private final int port;
    private final ITextIO io;

    public GameClient(String host, int port, ITextIO io) {
        this.host = host;
        this.port = port;
        this.io = io;
    }

    @Override
    public void run() {
        //Connect to server
        Socket s = null;
        try {
            s = new Socket(host, port);
            DataInput in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
            DataOutput out = new DataOutputStream(s.getOutputStream());
            String cmd = in.readUTF();
            while (!"close".equalsIgnoreCase(cmd)) {
                if ("put".equalsIgnoreCase(cmd)) {
                    String str = in.readUTF();
                    io.put(str);
                } else if ("clear".equalsIgnoreCase(cmd)) {
                    io.clear();
                } else if ("get".equalsIgnoreCase(cmd)) {
                    String res = io.get();
                    out.writeUTF(res);
                } else {
                    throw new RuntimeException("Unknown protocol command: " + cmd);
                }
                cmd = in.readUTF();
            }
            io.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException ex) {
                    // Nothing we can do about it.
                    // If we had an error log we would of course 
                    // log this error.
                }
            }
        }
    }

    public static void main(String[] args) {
        ITextIO io = new SysTextIO();
        io.put("Please enter server ip-adress: ");
        String adr = io.get();
        io.put("\nPlease enter server port: ");
        int port = io.getInteger(0, 65535);
        GameClient client = new GameClient(adr, port, io);
        client.run();
    }
}
