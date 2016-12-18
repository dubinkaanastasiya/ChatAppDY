import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.io.File;
import java.net.InetAddress;

public class MainForm extends JFrame {
    private static int choice = -1;
    static final int PORT = 28411;
    private JPanel allPanel;
    JTextField loginTextField;
    JLabel loginLabel;
    private JButton OKButton;
    JLabel remoteLoginLabel;
    JLabel remoteIPLabel;
    private JTextField remoteLoginTextField;
    JTextField remoteIPTextField;
    private JButton disconnectButton;
    private JButton connectButton;
    private JButton sendButton;
    JPanel centerPanel;
    JTextArea historyTextArea;
    JTextField messageTextField;
    JPanel southPanel;
    JPanel northPanel;
    JButton changeButton;
    JScrollPane historyScrollPane;
    private static Connection connection;
    private static Receiver receiver;

    private MainForm() {
        super("Big Daddy");
        setContentPane(allPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(this.getWidth(), this.getHeight() + 200);
        setLocationRelativeTo(null);
        setVisible(true);

        messageTextField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                if ((getLength() + str.length()) <= 512)
                    super.insertString(offset, str, attr);
                else
                    userMessage("You can't type more");
            }
        });

        File file = new File("IP.txt");
        try {
            if (!file.exists())
                file.createNewFile();
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (disconnectButton.isEnabled()) {
                    choice = optionUserMessage("Are you sure you want to leave the\n" +
                            "current chat and close the program?");

                    if (choice == 0) {
                        disconnectButton.doClick();
                        e.getWindow().setVisible(false);
                        System.exit(0);
                    }
                }
                else {
                    e.getWindow().setVisible(false);
                    System.exit(0);
                }
            }
            public void windowClosed(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });
        sendButton.addActionListener(e -> {
            if (!loginTextField.isEnabled() && !remoteIPTextField.isEnabled()) {
                if (!messageTextField.getText().isEmpty()) {
                    try {
                        connection.sendCommand(this, messageTextField.getText(), 2);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (loginTextField.isEnabled() || remoteIPTextField.isEnabled())
                messageTextField.setText(null);
        });
        OKButton.addActionListener(e -> {
            loginTextField.setText(loginTextField.getText().trim().replaceAll("[\\s]{2,}", " "));

            if (!loginTextField.getText().isEmpty()) {
                remoteIPTextField.setEnabled(true);
                remoteLoginTextField.setEnabled(true);
            }

            String myIP = null;
            InetAddress address;

            try {
                address = InetAddress.getLocalHost();
                myIP = address.getHostAddress();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (!loginTextField.getText().isEmpty())
                if (loginTextField.getText().length() > 10)
                    userMessage("You can enter only 10 characters");
                else if (!IPSaving.isAble(loginTextField.getText(), myIP))
                    userMessage("Enter another login");
                else {
                    loginTextField.setEnabled(false);
                    OKButton.setEnabled(false);
                    changeButton.setEnabled(true);
                    connectButton.setEnabled(true);

                    if (Receiver.pause) {
                        synchronized (receiver) {
                            receiver.notify();
                        }
                        Receiver.pause = false;
                    }
                }
        });
        connectButton.addActionListener(e -> {
            if (!loginTextField.isEnabled()) {
                remoteLoginTextField.setText(remoteLoginTextField.getText().trim().replaceAll("[\\s]{2,}", " "));
                remoteIPTextField.setText(remoteIPTextField.getText().trim().replaceAll("[\\s]{2,}", " "));

                if (!IPSaving.isSaved(remoteLoginTextField.getText())) {
                    if (!remoteLoginTextField.getText().isEmpty())
                        userMessage("This login isn`t saved, please, enter IP");

                    remoteLoginTextField.setText(null);
                    historyTextArea.setText(null);
                    remoteIPTextField.setEnabled(true);

                    if (!remoteIPTextField.getText().isEmpty())
                        connectionMessage();
                } else {
                    remoteIPTextField.setText(IPSaving.getIP());
                    connectionMessage();
                }
            }
        });
        disconnectButton.addActionListener(e -> {
            if (choice == 0)
                additionalDisconnect(false);
            else {
                choice = optionUserMessage("Are you sure you want to\nleave the current chat?");
                if (choice == 0)
                    additionalDisconnect(false);
            }
        });
        changeButton.addActionListener(e -> {
            changeButton.setEnabled(false);
            OKButton.setEnabled(true);
            loginTextField.setEnabled(true);
            remoteIPTextField.setEnabled(false);
            remoteLoginTextField.setEnabled(false);
            connectButton.setEnabled(false);
        });
        messageTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    sendButton.doClick();
            }
        });
        loginTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    OKButton.doClick();
            }
        });
        remoteLoginTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    connectButton.doClick();
            }
        });
        remoteIPTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    connectButton.doClick();
            }
        });
        remoteLoginTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (remoteLoginTextField.getText().isEmpty())
                    remoteIPTextField.setEnabled(true);
                else
                    remoteIPTextField.setEnabled(false);
            }
        });
        remoteIPTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (remoteIPTextField.getText().isEmpty())
                    remoteLoginTextField.setEnabled(true);
                else
                    remoteLoginTextField.setEnabled(false);
            }
        });
    }

    private void acceptCommand() {
        remoteIPTextField.setEnabled(false);
        connectButton.setEnabled(false);
        changeButton.setEnabled(false);
        disconnectButton.setEnabled(true);
    }

    void toEndOfArea() {
        historyTextArea.setCaretPosition(historyTextArea.getDocument().getLength());
    }
    private void userMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    private int optionUserMessage(String message) {
        Object[] options = { "Yes", "No" };
        return JOptionPane.showOptionDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    private void connectionMessage() {
        try {
            connection.sendCommand(this, null, 1);
        } catch (Exception ex) {
            userMessage("Can't connect to this user");
        }
    }

    private void additionalDisconnect(boolean myself) {
        if (!myself)
            try {
                connection.sendCommand(this, "User " + loginTextField.getText() + " from IP " +
                        InetAddress.getLocalHost().getHostAddress() + " disconnected", 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        disconnectButton.setEnabled(false);
        connectButton.setEnabled(true);
        remoteLoginTextField.setEnabled(true);
        remoteIPTextField.setEnabled(true);
        changeButton.setEnabled(true);
        remoteLoginTextField.setText(null);
        remoteIPTextField.setText(null);
    }

    void Request(Command cmd) throws java.lang.Exception {
        if (!connectButton.isEnabled()) {
            String tmp = remoteIPTextField.getText();
            remoteIPTextField.setText(((RequestCommand) cmd).IP);
            connection.sendCommand(this, "User " + loginTextField.getText() + " from IP " +
                    InetAddress.getLocalHost().getHostAddress() + " busy", 0);
            remoteIPTextField.setText(tmp);
        } else {
            int choice = optionUserMessage("Do you want to chat with\n" + ((RequestCommand) cmd).nick +
                    " from IP " + ((RequestCommand) cmd).IP + "?");

            if (choice == 0) {
                historyTextArea.setText(null);
                remoteLoginTextField.setText(((RequestCommand) cmd).nick);
                remoteIPTextField.setText(((RequestCommand) cmd).IP);
                connection.sendCommand(this, "User " + loginTextField.getText() + " from IP " +
                        InetAddress.getLocalHost().getHostAddress() + " accepted your request", 0);
                remoteLoginTextField.setEnabled(false);
                acceptCommand();

                if (!IPSaving.isSaved(remoteLoginTextField.getText()))
                    try {
                        IPSaving.writeData(remoteLoginTextField.getText(), remoteIPTextField.getText());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
            } else {
                remoteIPTextField.setText(((RequestCommand) cmd).IP);

                connection.sendCommand(this, "User " + loginTextField.getText() + " from IP " +
                        InetAddress.getLocalHost().getHostAddress() + " rejected", 0);
                additionalDisconnect(true);
            }
        }
    }

    void Message(Command cmd) {
        if (historyTextArea.getText().isEmpty())
            historyTextArea.setText(((MessageCommand) cmd).message);
        else
            historyTextArea.setText(historyTextArea.getText() + "\n" + ((MessageCommand) cmd).message);
        Sound.playSound();
    }

    void Reject(Command cmd) {
        userMessage("User " + ((RejectCommand) cmd).nick + " from IP " + ((RejectCommand) cmd).IP + " rejected your request");
        additionalDisconnect(true);
    }

    void Accept(Command cmd) {
        remoteLoginTextField.setText(((AcceptCommand) cmd).nick);
        remoteIPTextField.setText(((AcceptCommand) cmd).IP);
        userMessage("User " + ((AcceptCommand) cmd).nick + " from IP " + ((AcceptCommand) cmd).IP + " accepted your request");
        acceptCommand();
    }

    void Disconnect(Command cmd) {
        userMessage("User " + ((DisconnectCommand) cmd).nick + " from IP " + ((DisconnectCommand) cmd).IP + " disconnected");
        additionalDisconnect(true);
    }

    void Busy(Command cmd) {
        userMessage("User " + ((BusyCommand) cmd).nick + " from IP " + ((BusyCommand) cmd).IP + " is busy");
        additionalDisconnect(true);
    }

    public static void main(String[] args) throws Exception {
        javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        connection = new Connection();
        receiver = new Receiver();
        receiver.run(new MainForm(), connection);
    }
}