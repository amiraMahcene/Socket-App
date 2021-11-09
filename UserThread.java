import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserThread extends Thread {
    private final ServerChat server;
    private final Socket socket;

    private String username;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public UserThread(Socket socket, ServerChat server) {
        this.socket = socket;
        this.server = server;

        try {
            // Creation des flux de connexion
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            // On doit lire le nom d'utilisateur en premier
            username = dataInputStream.readUTF();
            // Ajouter l'utilisateur a la liste
            server.addUser(username);

            // Un message pour notifier tout les clients
            server.setAreaText(username + " has joined the chat");
            server.broadcastMessage("Server", username + " has joined the chat", this);

            while (true) {
                try {
                    String input = dataInputStream.readUTF();
                    server.setAreaText(String.format("[%s]: %s", username, input));

                    // On transfert le message ici
                    server.broadcastMessage(this.username, input, this);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            server.setAreaText("Message sending failed: Network Error");
            e.printStackTrace();
        } finally {
            server.removeUser(username, this);
            try {
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * La methode pour envoye un message au tant que cette utilisateur
     *
     * @param message message a envoye
     */
    public void sendMessage(String message) {
        try {
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deconnexion d'utilisateur
     *
     * @throws IOException Exception
     */
    public void disconnect() throws IOException {
        this.socket.close();
        dataOutputStream.close();
        dataInputStream.close();
        socket.close();
    }
}