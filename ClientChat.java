
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.*;

public class ClientChat extends JFrame implements ActionListener {
    static Socket socket;
    JPanel panel;
    JTextField textField;
    JTextArea textArea;
    JButton button;

    DataInputStream dis;
    DataOutputStream dos;
    String username;

    public ClientChat() throws IOException {
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
        panel.add(button);
        button.addActionListener(this);
        socket = new Socket(InetAddress.getLocalHost(), 8080);

        textArea.setText("Connected to Server");
        this.setTitle("Client");
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        // Creation d'un petite panneau pour recevoir utilisateur
        // Une boucle pour assurez un nom valide
        do {
            username = JOptionPane.showInputDialog(this, "Enter your username: ");
        } while (username == null);
        dos.writeUTF(username);

        while (true) {
            try {
                // On recoit le message d'apres le serveur
                String string = dis.readUTF();
                // Et on l'affiche
                setAreaText(string);
            } catch (Exception e1) {
                setAreaText("Message sending fail:Network Error");
                try {
                    Thread.sleep(3000);
                    System.exit(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Methode pour faciliter la manipulation du textArea
     *
     * @param message message a ajouter au panneau
     */
    public void setAreaText(String message) {
        textArea.setText(textArea.getText() + "\n" + message);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if ((e.getSource() == button) && (!textField.getText().equals(""))) {
            setAreaText("[Me]: " + textField.getText());
            try {
                // Puis on le transfert vers le serveur
                dos.writeUTF(textField.getText());
            } catch (Exception e1) {
                setAreaText("Message sending fail:Network Error");
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
        new ClientChat();
    }
}