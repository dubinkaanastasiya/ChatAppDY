import javax.swing.*;
import java.net.DatagramSocket;

class Receiver extends Thread {
    synchronized void run(MainForm f, Connection c) throws Exception {
        DatagramSocket receiveSocket = new DatagramSocket(MainForm.port);
        while (true) {
            /*if (f.getOKButton().isEnabled())
                try {
                    wait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/
            Command cmd = c.receiveCommand(receiveSocket);

            if (cmd instanceof NickCommand)
                switch (JOptionPane.showConfirmDialog(f, "Do you want to chat with " + ((NickCommand) cmd).nick + "?")) {
                    case JOptionPane.YES_OPTION:
                        f.getRemoteIPTextField().setText(((NickCommand) cmd).nick.substring(((NickCommand) cmd).nick.indexOf("IP") + 3));
                        new Connection().sendCommand(f, "User " + ((NickCommand) cmd).nick + " accepted your request for a connection" + System.lineSeparator(), 3);
                        f.getRemoteIPTextField().setEnabled(false);
                        f.getConnectButton().setEnabled(false);
                        f.getChangeButton().setEnabled(false);
                        f.getDisconnectButton().setEnabled(true);
                        f.getRemoteLoginTextField().setText(((NickCommand) cmd).nick.substring(0, ((NickCommand) cmd).nick.indexOf(" ")));
                        if (IPSaving.isAble( f.getRemoteLoginTextField().getText(), f.getRemoteIPTextField().getText()))
                            try {
                                IPSaving.writeData( f.getRemoteLoginTextField().getText(), f.getRemoteIPTextField().getText());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        break;
                    case JOptionPane.NO_OPTION:
                        new Connection().sendCommand(f, "Reject", 3);
                        f.getRemoteIPTextField().setText(null);
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        new Connection().sendCommand(f, "Reject", 3);
                        f.getRemoteIPTextField().setText(null);
                        break;
                }
            else if (cmd instanceof MessageCommand)
                if (f.getHistoryTextPane().getText().isEmpty())
                    f.getHistoryTextPane().setText(((MessageCommand) cmd).message + System.lineSeparator());
                else
                    f.getHistoryTextPane().setText(f.getHistoryTextPane().getText() + System.lineSeparator() + ((MessageCommand) cmd).message + System.lineSeparator());
            else if (cmd instanceof RejectCommand) {
                f.getHistoryTextPane().setText("User rejected your call");
                f.getRemoteIPTextField().setText(null);
                f.getRemoteLoginTextField().setText(null);
                f.getMessageTextField().setText(null);

                f.LoginVsIP();
                f.getConnectButton().setEnabled(true);
            }
            else if (cmd instanceof AcceptCommand) {
                f.getHistoryTextPane().setText("Connection established" + System.lineSeparator());
                f.getRemoteIPTextField().setEnabled(false);
                f.getConnectButton().setEnabled(false);
                f.getChangeButton().setEnabled(false);
                f.getDisconnectButton().setEnabled(true);
            }
            else if (cmd instanceof DisconnectCommand) {
                f.getHistoryTextPane().setText(f.getHistoryTextPane().getText() + System.lineSeparator() + "User disconnected");
                f.LoginVsIP();
                f.getConnectButton().setEnabled(true);
            }
            f.getHistoryTextPane().setCaretPosition(f.getHistoryTextPane().getDocument().getLength());
        }
    }
}