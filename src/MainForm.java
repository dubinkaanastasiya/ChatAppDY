import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainForm extends JFrame {
    private class receivingMessage extends Thread {
        @Override
        public synchronized void start() {
            try {
                DatagramSocket receiveSocket = new DatagramSocket(PORT);
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
                    receiveSocket.receive(receivePacket);
                    String message = new String(receivePacket.getData()).trim();

                    if (message.substring(0, message.indexOf("r") + 1).equals("ChatApp 2015 user")) {
                        if (JOptionPane.showConfirmDialog(null, "Do you want to chat with " + message.substring(message.indexOf("r") + 2) + "?") == JOptionPane.YES_OPTION) {
                            remoteLoginTextField.setText(message.substring(message.indexOf("r") + 2));

                            if (historyTextPane.getText().isEmpty()) {
                                historyTextPane.setText(message.substring(message.indexOf("r") + 2) + " connected");
                            } else {
                                historyTextPane.setText(historyTextPane.getText() + System.lineSeparator() + message.substring(message.indexOf("r") + 2) + " connected");
                            }
                        } else {
                            return;
                        }
                        message = null;
                    }
                    if (message != null) {
                        if (historyTextPane.getText().isEmpty()) {
                            historyTextPane.setText(message);
                        } else {
                            historyTextPane.setText(historyTextPane.getText() + System.lineSeparator() + message);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static final int PORT = 28411;
    private JPanel allPanel;
    private JTextField loginTextField;
    private JLabel loginLabel;
    private JButton OKButton;
    private JLabel remoteLoginLabel;
    private JLabel remoteIPLabel;
    private JTextField remoteLoginTextField;
    private JTextField remoteIPTextField;
    private JButton disconnectButton;
    private JButton connectButton;
    private JButton sendButton;
    private JPanel centerPanel;
    private JTextPane historyTextPane;
    private JTextField messageTextField;
    private JScrollPane historyScrollPane;
    private JPanel southPanel;
    private JPanel northPanel;
    private JButton clearButton;
    private JComboBox participantsComboBox;
    private JLabel participantsLabel;

    public MainForm() {
        super("Dub & Yak beta");
        setContentPane(allPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        sendButton.addActionListener(e -> {
            if (loginTextField.isEnabled()) {
                JOptionPane.showMessageDialog(null, "Enter login");
            } else if (remoteIPTextField.isEnabled()) {
                JOptionPane.showMessageDialog(null, "Enter address");
            } else {
                if (!messageTextField.getText().isEmpty()) {
                    try {
                        sendMessage();
                    } catch (Exception ex) {
                        System.out.println("Can't send message");
                    }
                }
            }
        });

        OKButton.addActionListener(e -> {
            loginTextField.setText(loginTextField.getText().trim());

            if (!loginTextField.getText().isEmpty()) {
                if (loginTextField.getText().length() > 10) {
                    JOptionPane.showMessageDialog(null, "You can enter only 10 characters");
                } else {
                    loginTextField.setEnabled(false);
                    OKButton.setEnabled(false);
                }
            }
        });

        connectButton.addActionListener(e -> {
            if (loginTextField.isEnabled()) {
                JOptionPane.showMessageDialog(null, "Enter login");
            } else {
                remoteIPTextField.setText(remoteIPTextField.getText().trim());

                if (remoteIPTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter address");
                } else {
                    remoteIPTextField.setEnabled(false);
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                }
                try {
                    DatagramSocket sendSocket = new DatagramSocket();
                    InetAddress IP = InetAddress.getByName(remoteIPTextField.getText());
                    String message = "ChatApp 2015 user " + loginTextField.getText();
                    byte[] sendData = message.getBytes("UTF-8");
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, PORT);
                    sendSocket.send(sendPacket);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Can't connect to this user");
                }
            }
        });

        disconnectButton.addActionListener(e -> {
            disconnectButton.setEnabled(false);
            remoteIPTextField.setEnabled(true);
            connectButton.setEnabled(true);
            OKButton.setEnabled(true);
            loginTextField.setEnabled(true);
        });

        messageTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });

        loginTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    OKButton.doClick();
                }
            }
        });

        remoteIPTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectButton.doClick();
                }
            }
        });
    }

    private void startThread() {
        new receivingMessage().start();
    }

    private void sendMessage() throws Exception {
        DatagramSocket sendSocket = new DatagramSocket();
        InetAddress IP = InetAddress.getByName(remoteIPTextField.getText());
        String message = loginTextField.getText() + ": " + messageTextField.getText();
        byte[] sendData = message.getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, PORT);
        sendSocket.send(sendPacket);

        if (historyTextPane.getText().isEmpty()) {
            historyTextPane.setText(loginTextField.getText() + ": " + messageTextField.getText());
        } else {
            historyTextPane.setText(historyTextPane.getText() + System.lineSeparator() + loginTextField.getText() + ": " + messageTextField.getText());
        }
        messageTextField.setText(null);
    }

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) {
            System.out.println("Error applying new style");
        }
        new MainForm().startThread();
    }
}