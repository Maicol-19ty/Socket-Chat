package cue.edu.co.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import cue.edu.co.protocol.*;

public class Menu {

    // Static array list for storing the list of friends
    public static ArrayList<Peer> friend_list = null;

    // Handles communication with the server and chat requests
    private InetAddress server_ip_addr;
    private int server_port = 8080;

    private int peer_port = 10000;
    private String username = "";

    private Socket client_socket;
    private boolean isStop = false;
    private Chat server;

    private ObjectInputStream listener;
    private ObjectOutputStream sender;

    // Main constructor initializes connection details and starts threads
    public Menu(String server_ip, int server_port, int peer_port, String username, String dataUser) throws Exception {
        this.server_ip_addr = InetAddress.getByName(server_ip);
        this.server_port = server_port;
        this.username = username;
        this.peer_port = peer_port;
        friend_list = Decode.getAllAccount(dataUser);

        new Thread(new Runnable(){

            @Override
            public void run() {
                updateFriendList();
            }
        }).start();
        server = new Chat(username, peer_port);
        (new Request()).start();
    }

    // Thread class for handling requests to the server
    public class Request extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isStop) {
                try {
                    Thread.sleep(1500);

                    SocketAddress addressServer = new InetSocketAddress(server_ip_addr, server_port);
                    client_socket = new Socket(); client_socket.connect(addressServer);
                    String msg = Encode.genOnlineMessage(username);
                    sender = new ObjectOutputStream(client_socket.getOutputStream());
                    sender.writeObject(msg); sender.flush();
                    listener = new ObjectInputStream(client_socket.getInputStream());
                    msg = (String) listener.readObject(); listener.close();
                    friend_list = Decode.getAllAccount(msg);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            updateFriendList();
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Requests a chat with a friend
    public void requestChat(String IP, int host, String guest) throws Exception {
        final Socket connclient = new Socket(InetAddress.getByName(IP), host);
        ObjectOutputStream sendrequestChat = new ObjectOutputStream(connclient.getOutputStream());
        sendrequestChat.writeObject(Encode.genChatRequest(username));
        sendrequestChat.flush();
        ObjectInputStream receivedChat = new ObjectInputStream(connclient.getInputStream());
        String msg = (String) receivedChat.readObject();
        if (msg.equals(Tags.CHAT_DENY_TAG)) {
            MenuGUI.showDialog("May be your friend busy!", false);
            connclient.close();
            return;
        } else {
            new ChatGUI(username, guest, connclient, peer_port);
        }
    }

    // Requests to exit the chat application
    public void requestExit() throws IOException, ClassNotFoundException {
        isStop = true;
        client_socket = new Socket();
        SocketAddress addressServer = new InetSocketAddress(server_ip_addr, server_port);
        client_socket.connect(addressServer);
        // Send offline message
        String msg = Encode.genOfflineMessage(username);
        sender = new ObjectOutputStream(client_socket.getOutputStream());
        sender.writeObject(msg); sender.flush(); sender.close();
        // Close chat room
        server.exit();
    }

    // Updates the friend list in the GUI
    public void updateFriendList() {
        int size = friend_list.size();
        MenuGUI.clearFriendList();
        for (int i = 0; i < size; i++)
            if (!friend_list.get(i).getName().equals(username))
                MenuGUI.updateFiendList(friend_list.get(i).getName());
    }
}
