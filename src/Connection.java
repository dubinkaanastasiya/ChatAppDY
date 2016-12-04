import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
    private BufferedReader in;
    private PrintWriter out;
    private Socket s;
    private NickCommand nc = new NickCommand("", false);

    public Connection(Socket s)
    {
        this.s = s;
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public void accept()
    {
        out.println("Accepted");
    }

    public void close()
    {
        close();
    }

    public void disconnect() {
        out.println("Disconnected");
        close();
    }

    public boolean isOpen() {
        if (s != null) {
            return true;
        }
        return false;
    }

    public Command receive() throws IOException {
        String s = in.readLine();

        if (s.equals("Accepted"))
            return new AcceptCommand();
        if (s.equals("Disconnected"))
            return new DisconnectCommand();
        if (s.equals("Message")) {
            String message = in.readLine();
            return new MessageCommand(message);
        }
        if (s.substring(0, 18).equals("ChatApp 2015 user ")) {
            String nick = s.substring(18);
            boolean busy;
            if (s.substring(s.length()-1-4, s.length()-1).equals("busy"))
                busy = true;
            else
                busy = false;
            return new NickCommand(nick, busy);
        }
        else
            return new RejectCommand();
    }

    public void reject()
    {
        out.println("Rejected");
        close();
    }

    public void sendMessage(String msg)
    {
        out.println("Message");
        out.println(msg);
    }

    public void sendNickBusy(String nick)
    {
        if (isOpen()) {
            out.println("ChatApp 2015 user " + nick + " busy");
        }
    }

    public void sendNickHello(String nick)
    {
        if (s != null) {
            out.println("ChatApp 2015 user " + nick);
        }
    }


    public static void main(String[] args) {
    }
}