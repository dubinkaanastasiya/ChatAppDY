import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainForm extends JFrame {
    static final int port = 28411;
    private JPanel allPanel;
    private JTextField loginTextField;
    JLabel loginLabel;
    private JButton OKButton;
    JLabel remoteLoginLabel;
    JLabel remoteIPLabel;
    private JTextField remoteLoginTextField;
    private JTextField remoteIPTextField;
    private JButton disconnectButton;
    private JButton connectButton;
    private JButton sendButton;
    JPanel centerPanel;
    private JTextPane historyTextPane;
    private JTextField messageTextField;
    JScrollPane historyScrollPane;
    JPanel southPanel;
    JPanel northPanel;
    private JButton changeButton;

    private MainForm() {
        super("Original Chat");
        setContentPane(allPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        sendButton.addActionListener(e -> {
            if (!loginTextField.isEnabled() && !remoteIPTextField.isEnabled()) {
                if (!messageTextField.getText().isEmpty()) {
                    try {
                        new Connection().sendCommand(this, messageTextField.getText(), false);
                    } catch (Exception ex) {
                        System.out.println("Can't send message");
                    }
                }
            }
        });

        OKButton.addActionListener(e -> {
            loginTextField.setText(loginTextField.getText().trim());

            if (!loginTextField.getText().isEmpty())
                if (loginTextField.getText().length() > 10)
                    JOptionPane.showMessageDialog(null, "You can enter only 10 characters");
                else {
                    loginTextField.setEnabled(false);
                    OKButton.setEnabled(false);
                    changeButton.setEnabled(true);
                }
        });

        connectButton.addActionListener(e -> {
            if (!loginTextField.isEnabled()) {
                remoteIPTextField.setText(remoteIPTextField.getText().trim());
                if (!remoteIPTextField.getText().isEmpty()) {
                    // in other place
                    remoteIPTextField.setEnabled(false);
                    connectButton.setEnabled(false);
                    changeButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                    ///////////////////////////////////////////////////////////////////

                    try {
                        new Connection().sendCommand(this, null, true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Can't connect to this user");
                    }
                }
            }
        });

        disconnectButton.addActionListener(e -> {
            disconnectButton.setEnabled(false);
            remoteIPTextField.setEnabled(true);
            connectButton.setEnabled(true);
            changeButton.setEnabled(true);
            remoteLoginTextField.setText(null);
            remoteIPTextField.setText(null);
            historyTextPane.setText(null);
        });

        changeButton.addActionListener(e -> {
            loginTextField.setEnabled(true);
            OKButton.setEnabled(true);
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

        remoteIPTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    connectButton.doClick();
            }
        });
    }

    JTextPane getHistoryTextPane() {
        return historyTextPane;
    }

    JTextField getRemoteLoginTextField() {
        return remoteLoginTextField;
    }

    JTextField getRemoteIPTextField() {
        return remoteIPTextField;
    }

    JTextField getLoginTextField() {
        return loginTextField;
    }

    JTextField getMessageTextField() {
        return messageTextField;
    }

    JButton getConnectButton() {
        return connectButton;
    }

    JButton getDisconnectButton() {
        return disconnectButton;
    }

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) {
            System.out.println("Error applying new style");
        }
        MainForm form = new MainForm();
        try {
            new Receiver().run(form, new Connection());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}