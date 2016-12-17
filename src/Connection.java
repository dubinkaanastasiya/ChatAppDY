import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Connection {
    void sendCommand(MainForm f, String message, int messageType) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        DatagramSocket sendSocket = new DatagramSocket();
        InetAddress IP = InetAddress.getByName(f.remoteIPTextField.getText());

        String s = message;
        if (messageType == 1)
            s = "ChatApp 2015 user " + f.loginTextField.getText() + " from IP " + InetAddress.getLocalHost().getHostAddress();
        else if (messageType == 2)
            s = "Message " + f.loginTextField.getText() + " [" + formatter.format(new Date()) + "]\n" + message.trim();

        byte[] sendData = s.getBytes("Cp1251");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, MainForm.PORT);
        sendSocket.send(sendPacket);

        if (messageType == 2) {
            if (f.historyTextPane.getText().isEmpty())
                f.historyTextPane.setText(s.substring(8));
            else
                f.historyTextPane.setText(f.historyTextPane.getText() + "\n" + s.substring(8));
        }
        f.messageTextField.setText(null);
        f.toEndOfPane();
    }

    Command receiveCommand(DatagramSocket socket) {
        String command;
        try {
            DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
            socket.receive(receivePacket);
            command = new String(receivePacket.getData()).trim();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        if (command.length() > 16)
            if (command.substring(0, 17).equals("ChatApp 2015 user"))
                return new RequestCommand(command.substring(18, command.indexOf("from") - 1),
                        command.substring(command.indexOf("IP") + 3));
        if (command.length() > 6)
            if (command.substring(0, 7).equals("Message"))
                return new MessageCommand(command.substring(8));
        if (command.contains("rejected"))
            return new RejectCommand(command.substring(5, command.indexOf("from") - 1),
                    command.substring(command.indexOf("IP") + 3, command.indexOf("rejected") - 1));
        if (command.contains("accepted"))
            return new AcceptCommand(command.substring(5, command.indexOf("from") - 1),
                    command.substring(command.indexOf("IP") + 3, command.indexOf("accepted") - 1));
        if (command.contains("disconnected"))
            return new DisconnectCommand(command.substring(5, command.indexOf("from") - 1),
                    command.substring(command.indexOf("IP") + 3, command.indexOf("disconnected") - 1));
        if (command.contains("busy"))
            return new BusyCommand(command.substring(5, command.indexOf("from") - 1),
                    command.substring(command.indexOf("IP") + 3, command.indexOf("busy") - 1));
        return null;
    }
}