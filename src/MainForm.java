import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainForm extends JFrame {
    // thread for receiving message
    private class Receiver extends Thread {
        @Override
        public void start() {
            try {
                DatagramSocket receiveSocket = new DatagramSocket(PORT);
                while (true) {
                    // receiving message
                    DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
                    receiveSocket.receive(receivePacket);
                    historyTextPane.setText(historyTextPane.getText() + System.lineSeparator() + new String(receivePacket.getData()).trim());
                }
            } catch (Exception ex) {
                System.out.println("Can't create receiving socket");
            }
        }
    }
    static int PORT = 28411;
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
    private JComboBox participantsComboBox;
    private JLabel participantsLabel;
    private String old_login;
    private boolean need_change;

    public MainForm() {
        super("ChatAppDY v0.2");
        setContentPane(allPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        sendButton.addActionListener(e -> {
            if (loginTextField.isEnabled()) {
                JOptionPane.showMessageDialog(null, "Please, enter login.");
            } else if (remoteIPTextField.isEnabled()) {
                JOptionPane.showMessageDialog(null, "Please, enter IP.");
            } else {
                if (!messageTextField.getText().isEmpty()) {
                    if (historyTextPane.getText().isEmpty()) {
                        historyTextPane.setText(loginTextField.getText() + ": " + messageTextField.getText().trim());
                    } else {
                        historyTextPane.setText(historyTextPane.getText() + System.lineSeparator() + loginTextField.getText() + ": " + messageTextField.getText().trim());
                    }
                    messageTextField.setText(null);
                }
                try {
                    String sentence = messageTextField.getText();
                    messageTextField.setText(null);
                    DatagramSocket sendSocket = new DatagramSocket();
                    InetAddress IP = InetAddress.getByName("192.168.0.103");
                    DatagramPacket sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IP, PORT);
                    sendSocket.send(sendPacket);
                    historyTextPane.setText(historyTextPane.getText() + System.lineSeparator() + sentence);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        OKButton.addActionListener(e -> {
            loginTextField.setText(loginTextField.getText().trim());

            if (loginTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please, enter login.");
            } else {
                if (loginTextField.getText().length() > 10) {
                    loginTextField.setText(loginTextField.getText().substring(0, 10));
                }
                if (OKButton.getText().equals("Change")) {
                    loginTextField.setEnabled(true);
                    OKButton.setText("OK");
                } else {
                    if (need_change) {
                        historyTextPane.setText(historyTextPane.getText().replaceAll(old_login, loginTextField.getText()));
                    }

                    old_login = loginTextField.getText();
                    need_change = true;
                    loginTextField.setEnabled(false);
                    OKButton.setText("Change");
                }
            }
        });
        connectButton.addActionListener(e -> {
            if (loginTextField.isEnabled()) {
                JOptionPane.showMessageDialog(null, "Please, enter login.");
            } else {
                remoteIPTextField.setText(remoteIPTextField.getText().trim());

                if (remoteIPTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please, enter IP.");
                } else {
                    remoteIPTextField.setEnabled(false);
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                }
            }
        });
        disconnectButton.addActionListener(e -> {
            remoteIPTextField.setEnabled(true);
            disconnectButton.setEnabled(false);
            connectButton.setEnabled(true);
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
    public void TS() {
        new Receiver().start();
    }
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) {
            System.out.println("Can't apply new style for application");
        }
        new MainForm().TS();
    }
}