package cue.edu.co.server;

import mdlaf.MaterialLookAndFeel;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import javax.swing.JTextArea;

public class ServerGUI {

    // Static initializer block to set the MaterialLookAndFeel
    static {
        try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel());
            UIManager.put("Button.mouseHoverEnable", true);
            JFrame.setDefaultLookAndFeelDecorated(false);

        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private static String URL_DIR = System.getProperty("user.dir");

    private JFrame fmServer;
    private JTextField txtIP, txtPort;
    private static JTextArea txtMessage;
    private static JLabel lbNumber;
    Server server;

    // Constructor to initialize the Server GUI
    public ServerGUI() {
        initializeFrame();
        initializeLabel();
        initializeTextBox();
        initializeButton();
    }

    // Method to initialize the main JFrame
    private void initializeFrame() {
        fmServer = new JFrame();
        fmServer.setTitle("Server Controller");
        fmServer.setResizable(false);
        fmServer.setBounds(200, 200, 550, 442);
        fmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fmServer.getContentPane().setLayout(null);
    }

    // Method to initialize text labels
    private void initializeLabel() {
        JLabel lbIP = new JLabel("IP :");
        lbIP.setBounds(5, 10, 40, 16);
        fmServer.getContentPane().add(lbIP);

        JLabel lbPort = new JLabel("PORT : ");
        lbPort.setBounds(285, 10, 50, 16);
        fmServer.getContentPane().add(lbPort);

    }

    // Method to initialize text input fields
    private void initializeTextBox() {
        txtIP = new JTextField();
        txtIP.setBounds(40, 9, 210, 28);
        fmServer.getContentPane().add(txtIP);
        txtIP.setColumns(10);
        txtIP.setText("localhost");


        txtPort = new JTextField();
        txtPort.setColumns(10);
        txtPort.setBounds(336, 9, 208, 28);
        fmServer.getContentPane().add(txtPort);
        txtPort.setText("8080");

        txtMessage = new JTextArea();
        txtMessage.setEditable(false);
        txtMessage.setFont(new Font("Hacker",Font.BOLD,10));
        txtMessage.setBounds(5, 130, 540, 270);
        fmServer.getContentPane().add(txtMessage);
    }

    // Method to initialize buttons
    private void initializeButton() {
        JButton btnStart = new JButton("START");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    String ip = txtIP.getText();
                    int port = Integer.parseInt(txtPort.getText());
                    server = new Server(ip, port);
                    ServerGUI.updateMessage("START SERVER");
                } catch (Exception e) {
                    ServerGUI.updateMessage("START ERROR");
                    e.printStackTrace();
                }
            }
        });
        btnStart.setBounds(5, 90, 260, 29);
        fmServer.getContentPane().add(btnStart);

        JButton btnStop = new JButton("STOP");
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    server.stop();
                    ServerGUI.updateMessage("STOP SERVER");
                } catch (Exception e) {
                    e.printStackTrace();
                    ServerGUI.updateMessage("STOP SERVER");
                }
            }
        });
        btnStop.setBounds(285, 90, 260, 29);
        fmServer.getContentPane().add(btnStop);
    }

    // Main method to launch the Server GUI
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ServerGUI window = new ServerGUI();
                    window.fmServer.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Method to update the message displayed in the JTextArea
    public static void updateMessage(String msg) {
        txtMessage.append(msg + "\n");
    }

    // Method to update the number of clients displayed
    public static void updateNumberClient() {
        int number = Integer.parseInt(lbNumber.getText());
        lbNumber.setText(Integer.toString(number + 1));
    }
}
