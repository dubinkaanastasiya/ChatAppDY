import javax.swing.*;
import java.net.DatagramSocket;

class Receiver extends Thread {
    synchronized void run(MainForm f, Connection c) throws Exception {
        DatagramSocket receiveSocket = new DatagramSocket(MainForm.port);
        while (true) {
            Command cmd = c.receiveCommand(receiveSocket);

            if (cmd instanceof NickCommand) {
                switch (JOptionPane.showConfirmDialog(null, "Do you want to chat with " + ((NickCommand) cmd).nick + "?")) {
                    case JOptionPane.YES_OPTION:
                        f.getHistoryTextPane().setText(((NickCommand) cmd).nick + " connected" + System.lineSeparator());
                        f.getConnectButton().setEnabled(false);
                        f.getDisconnectButton().setEnabled(true);
                        f.getRemoteLoginTextField().setText(((NickCommand) cmd).nick);
                        break;
                    case JOptionPane.NO_OPTION:
                    case JOptionPane.CANCEL_OPTION:
                        try {
                            new Connection().sendCommand(f, "User doesn't want to chat", false);
                        } catch (Exception ex) {
                            System.out.println("Can't send message");
                        }
                        break;
                }
            } else if (cmd instanceof MessageCommand) {
                if (f.getHistoryTextPane().getText().isEmpty())
                    f.getHistoryTextPane().setText(((MessageCommand) cmd).message + System.lineSeparator());
                else
                    f.getHistoryTextPane().setText(f.getHistoryTextPane().getText() + System.lineSeparator() + ((MessageCommand) cmd).message + System.lineSeparator());
            }
            f.getHistoryTextPane().setCaretPosition(f.getHistoryTextPane().getDocument().getLength());
        }
    }
}