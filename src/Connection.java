import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Connection {
    void sendCommand(MainForm f, String message, int messageType) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        DatagramSocket sendSocket = new DatagramSocket();
        InetAddress IP = InetAddress.getByName(f.getRemoteIPTextField().getText());

        String text = message;
        switch (messageType) {
            case 1:
                text = "ChatApp 2015 user " + f.getLoginTextField().getText() + " from IP " + InetAddress.getLocalHost().getHostAddress();
                break;
            case 2:
                text = "Message [" + formatter.format(new Date()) + "] " + f.getLoginTextField().getText() + ": " + message + System.lineSeparator();
                break;
        }

        byte[] sendData = text.getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, MainForm.port);
        sendSocket.send(sendPacket);

        if (messageType == 2) {
            if (f.getHistoryTextPane().getText().isEmpty())
                f.getHistoryTextPane().setText(text.substring(8));
            else
                f.getHistoryTextPane().setText(f.getHistoryTextPane().getText() + System.lineSeparator() + text.substring(8));
        }

        f.getMessageTextField().setText(null);
        f.getHistoryTextPane().setCaretPosition(f.getHistoryTextPane().getDocument().getLength());
    }

    Command receiveCommand(DatagramSocket socket) {
        String command = null;

        try {
            DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
            socket.receive(receivePacket);
            command = new String(receivePacket.getData()).trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (command != null) {
            if (command.length() > 16)
                if (command.substring(0, 17).equals("ChatApp 2015 user"))
                    return new NickCommand(command.substring(18));
            if (command.length() > 6)
                if (command.substring(0, 7).equals("Message"))
                    return new MessageCommand(command.substring(8));
            if (command.equals("Reject"))
                return new RejectCommand();
            if (command.contains("accepted your request for a connection"))
                return new AcceptCommand();
            if (command.equals("Disconnect"))
                return new DisconnectCommand();
        }
        return null;
    }
}