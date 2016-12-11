import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainForm extends JFrame {
    private static int choice = -1;
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
    private static Receiver receiver;

    private MainForm() {
        super("Elegant Chat");
        setContentPane(allPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        File file = new File("IP.txt");
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (disconnectButton.isEnabled()) {
                    Object[] options = { "Yes", "No" };
                    choice = JOptionPane.showOptionDialog(e.getWindow(), "Are you sure you want to leave the\n" +
                                    "current chat and close the program?",
                            "Confirm", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options,
                            options[0]);
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
                        new Connection().sendCommand(this, messageTextField.getText(), 2);
                    } catch (Exception ex) {
                        System.out.println("Can't send message");
                    }
                }
            } else if (loginTextField.isEnabled() || remoteIPTextField.isEnabled())
                messageTextField.setText(null);
        });

        OKButton.addActionListener(e -> {
            String myLANIP;
            InetAddress addr=null;
            try {
                addr = InetAddress.getLocalHost();
            }
            catch (UnknownHostException ex)
            {
                ex.printStackTrace();
            }
            myLANIP = addr.getHostAddress();

            if (!loginTextField.getText().isEmpty())
                if (loginTextField.getText().length() > 10)
                    JOptionPane.showMessageDialog(null, "You can enter only 10 characters");
                else if (!IPSaving.isAble(loginTextField.getText(), myLANIP))
                        JOptionPane.showMessageDialog(null, "Enter another login");
                    else
                        LoginVsIP();
        });

        connectButton.addActionListener(e -> {
            if (!loginTextField.isEnabled()) {
                remoteLoginTextField.setText(remoteLoginTextField.getText().trim());
                remoteIPTextField.setText(remoteIPTextField.getText().trim());

                if (!IPSaving.isSaved(remoteLoginTextField.getText()) && (remoteIPTextField.getText().isEmpty())) {
                    JOptionPane.showMessageDialog(null, "This login isn`t saved, please, enter IP");
                    remoteLoginTextField.setEditable(false);
                    remoteLoginTextField.setText("");
                    remoteIPTextField.setEditable(true);
                    if (!remoteIPTextField.getText().isEmpty())
                    {
                        try {
                            new Connection().sendCommand(this, null, 1);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Can't connect to this user");
                        }
                    }
                }
                else
                {
                    remoteIPTextField.setText(IPSaving.getIP());

                    try {
                        new Connection().sendCommand(this, null, 1);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Can't connect to this user");
                    }
                }
            }
        });

        disconnectButton.addActionListener(e -> {
            if (choice == 0)
                additionalDisconnect();
            else {
                Object[] options = { "Yes", "No" };
                choice = JOptionPane.showOptionDialog(this, "Are you sure you want to\nleave the current chat?",
                        "Confirm", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);

                if (choice == 0)
                    additionalDisconnect();
            }
        });

        changeButton.addActionListener(e -> {
            changeButton.setEnabled(false);
            OKButton.setEnabled(true);
            loginTextField.setEnabled(true);
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
        messageTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageTextField.getText().equals("Write a message..."))
                    messageTextField.setText(null);
            }
        });
        messageTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (messageTextField.getText().isEmpty())
                    messageTextField.setText("Write a message...");
            }
        });
    }

    void LoginVsIP()
    {
        Object[] options = { "Login", "IP" };
        while(choice==-1)
        {
        choice = JOptionPane.showOptionDialog(this, "Do you want to enter\n" +
                        "login or IP to connect?",
                "Confirm", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options,
                options[0]);}
        if (choice == 0) {
            remoteLoginTextField.setEditable(true);
            remoteIPTextField.setEditable(false);
        }
        else {
            remoteLoginTextField.setEditable(false);
            remoteIPTextField.setEditable(true);
        }

        loginTextField.setEnabled(false);
        OKButton.setEnabled(false);
        changeButton.setEnabled(true);
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

    JButton getChangeButton() {
        return changeButton;
    }

    JButton getOKButton(){ return OKButton; }

    /*JButton getOKButton() {
        return OKButton;
    }*/

    private void additionalDisconnect() {
        try {
            new Connection().sendCommand(this, "Disconnect", 3);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        disconnectButton.setEnabled(false);
        remoteIPTextField.setEnabled(true);
        changeButton.setEnabled(true);
        remoteLoginTextField.setText(null);
        remoteIPTextField.setText(null);
        historyTextPane.setText(null);
    }

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) {
            System.out.println("Error applying new style");
        }

        receiver = new Receiver();
        try {
            receiver.run(new MainForm(), new Connection());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}