import java.net.DatagramSocket;

class Receiver extends Thread {
    static boolean pause = true;
    private static DatagramSocket receiveSocket;

    synchronized void run(MainForm f, Connection c) throws java.lang.Exception {
        while (true) {
            if (pause) {
                wait();
                receiveSocket = new DatagramSocket(MainForm.PORT);
            }
            Command cmd = c.receiveCommand(receiveSocket);

            if (cmd instanceof RequestCommand)
                f.Request(cmd);
            else if (cmd instanceof MessageCommand)
                f.Message(cmd);
            else if (cmd instanceof RejectCommand)
                f.Reject(cmd);
            else if (cmd instanceof AcceptCommand)
                f.Accept(cmd);
            else if (cmd instanceof DisconnectCommand)
                f.Disconnect(cmd);
            else if (cmd instanceof BusyCommand)
                f.Busy(cmd);

            f.toEndOfArea();
        }
    }
}