package cue.edu.co.client;

import cue.edu.co.protocol.Decode;
import cue.edu.co.protocol.Tags;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Chat {

    // Attributes
    private String username = ""; // Customer username
    private int port = 0; // Port on which the client listens for incoming connections
    private ServerSocket serverPeer; // Server socket to accept incoming connections
    private boolean isStop = false; // Flag to stop server execution

    // Class constructor
    public Chat(String username, int port) throws Exception {
        this.username = username;
        this.port = port;
        serverPeer = new ServerSocket(port); // Create a new ServerSocket on the specified port
        (new WaitPeerConnect()).start(); // Start a thread to wait for incoming connections
    }

    // Method to exit the chat
    public void exit() throws IOException {
        isStop = true;
        serverPeer.close(); // Close the server socket
    }

    // Internal class to handle incoming connections
    class WaitPeerConnect extends Thread {

        Socket connection;
        ObjectInputStream getRequest; //Stream to receive data from the connection

        @Override
        public void run() {
            super.run();
            while (!isStop) {
                try {
                    connection = serverPeer.accept(); // Accept an incoming connection
                    getRequest = new ObjectInputStream(connection.getInputStream());
                    String msg = (String) getRequest.readObject();
                    String name = Decode.getNameRequestChat(msg); // Extract the username from the message
                    // Display a dialog to ask the user if they want to chat
                    int result = MenuGUI.showDialog("Would you like chat with " + name, true);
                    ObjectOutputStream send = new ObjectOutputStream(connection.getOutputStream());
                    if (result == 0) {
                        send.writeObject(Tags.CHAT_ACCEPT_TAG);
                        new ChatGUI(username, name, connection, port);
                    } else if (result == 1) {
                        send.writeObject(Tags.CHAT_DENY_TAG);
                    }
                    send.flush();
                } catch (Exception e) {
                    break;
                }
            }
            try {
                serverPeer.close(); // Close the server socket when stopped
            } catch (IOException e) {
                e.printStackTrace(); //Handle any socket closing exceptions
            }
        }
    }
}
