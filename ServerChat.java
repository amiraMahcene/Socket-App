
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerChat extends JFrame implements ActionListener {
    static ServerSocket server;
    JPanel panel;
    JTextField textField;
    JTextArea textArea;
    JButton button;

    // List des threads des utilisateurs connectes
    private Set<UserThread> users;
    // List des noms des utilisateurs, utilisee pour indexation et supression
    private Set<String> usernames;


    /**
     * @param port port utilisee pour la connexion
     */
    public ServerChat(int port) throws IOException {
        panel = new JPanel();
        textField = new JTextField();
        textArea = new JTextArea();
        button = new JButton("Send");
        this.setSize(500, 500);
        this.setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel.setLayout(null);
        this.add(panel);
        textArea.setBounds(20, 20, 450, 360);
        panel.add(textArea);
        textField.setBounds(20, 400, 340, 30);
        panel.add(textField);
        button.setBounds(375, 400, 95, 30);
        button.setBackground(Color.blue);
        panel.add(button);
        this.setTitle("Server");
        button.addActionListener(this);
        server = new ServerSocket(port);
        textArea.setText("Waiting for Client");

        users = new HashSet<>();
        usernames = new HashSet<>();

        while (true) {
            // Listening to all incoming clients
            try {
                Socket socket = server.accept();
                // Create new connection thread
                UserThread user = new UserThread(socket, this);
                users.add(user);
                user.start();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
//distrocteur a la fin the la la class
    //la3akes ta3 constructor +++
    protected void finalize() throws IOException {
        server.close();
    }

    public void addUser(String username) {
        usernames.add(username);
    }

    public void removeUser(String username, UserThread user) {
        // does the user exist?
        boolean removed = usernames.remove(username);
        if (removed) {
            // if he exists remove from both lists
            users.remove(user);
            try {
                // force disconnection here
                user.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAreaText(String message) {
        textArea.setText(textArea.getText() + "\n" + message);
    }

    /**
     * trasfert d'un message vert tout les clients
     *
     * @param username utilisateur qui a envoye le message
     * @param message  le message a envoye
     */
    public void broadcastMessage(String username, String message) {
        for (UserThread user : users) {
            user.sendMessage(String.format("[%s]: %s", username, message));
        }
    }

    public void broadcastMessage(String username, String message, UserThread excludedUser) {
        for (UserThread user : users) {
            if (user != excludedUser)
                user.sendMessage(String.format("[%s]: %s", username, message));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if ((e.getSource() == button) && (!textField.getText().equals(""))) {
            setAreaText("[Server]: " + textField.getText());
            try {
                broadcastMessage("Server", textField.getText());
            } catch (Exception e1) {
                try {
                    Thread.sleep(3000);
                    System.exit(0);
                } catch (InterruptedException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }
            textField.setText("");
        }
    }

    public static void main(String[] args) throws IOException {
        new ServerChat(8080);
    }
}