/*
 * Author: Mikkel Husted
 * Date: 26/09-22
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;

import javax.swing.ImageIcon;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFrame;

import java.net.*;
import java.io.*;

import java.net.Socket;
import java.net.ServerSocket;
/*
 * This is a very basic version of MSN. 
 * User will write their username, ip adress of them self, and of the person who they want to connect to.
 * Then they can send messages back and forth.
 * 
 */

public class Menu implements ActionListener, ItemListener  {
    JTextArea output;
    JScrollPane scrollPane;
    String newline = "\n";
    String userName= null;
    String message = null;
    int portNr = 0;
    String[] serverIpArr = null;
    String clientIp = null;
    String serverIp = null;
    JFrame frame = null;
    Boolean portNrSet = false;
    Boolean userNameSet = false;

    //Server Stuff1
    InetAddress localAddress = null;
    PrintStream out = null;
    BufferedReader in = null;
    Socket clientSocket = null;
    Boolean clientSocketSet = false;    
    ServerSocket serverSocket = null;
    Boolean setEchoServer = false;

    //Thread stuff
    Thread thread_echoServer = null;

    class Control{
        public volatile boolean killEchoServerFlag = false;
        public volatile boolean EchoServerFlag = false;
    }
    final Control control = new Control();

    public void setName (String name){
        try {
            this.userNameSet = true;
            this.userName = name;
        } catch (Exception e) {
            String s = "Error:" + e.getMessage();
            output.append(s + newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
    }

    public void setMessage(String message) {
        try {
            this.message = message;
        } catch (Exception e) {
            String s = "Error:" + e.getMessage();
            output.append(s + newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
    }

    public void setportNr(String port) {
        try {
            int Port = Integer.parseInt(port);
            if (Port > 1023){
                this.portNr = Port;
                this.portNrSet = true;
                output.append("Port number was set to:" + port + newline);
                output.setCaretPosition(output.getDocument().getLength());
            }
            else{
                String s = "Error:" + newline
                + "Please only enter a valid port nr in range from 1024 - 65000";
                output.append(s + newline);
                output.setCaretPosition(output.getDocument().getLength());    
            }
        } catch (Exception e) {
            String s = "Error:" + e.getMessage() + newline
            + "Please only enter a valid port nr";
            output.append(s + newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
    }

    public void setServerIpAdress(String ip) {
        try {
            ip.contains(".");
        } catch (Exception e) {
            String s = "Error:" + e.getMessage() + newline
            + "Please only enter a valid string";
            output.append(s + newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
        try {
            this.serverIp = ip;
            this.serverIpArr = ip.split(".");
        } catch (Exception e) {
            String s = "Error:" + e.getMessage();
            output.append(s + newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
        
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbEchoServer;
        ImageIcon icon = createImageIcon("images/middle.gif");
        Object[] nullOptions = null;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("Settings");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "General settings");
        menuBar.add(menu);

        //Sets the user name, the name to be displayed when we send a message
        menuItem = new JMenuItem("Set user name", KeyEvent.VK_2);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This sets the public username.");
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String s = (String)JOptionPane.showInternalInputDialog(
                    frame.getContentPane(),
                    "Please enter your username.",
                    "Set Username Dialog",JOptionPane.PLAIN_MESSAGE, icon,nullOptions,null);
                    //If a string was returned, say so.
                    if ((s != null) && (s.length() > 0)) {
                        setName(s);
                        output.append("Username was set to:" + s + newline);
                        output.setCaretPosition(output.getDocument().getLength());
                        return;
                    }
            }
        });
        menu.add(menuItem);

        // This sets the port number from which we wanna talk
        menuItem = new JMenuItem("Set Port number", KeyEvent.VK_1);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This sets the Port number");
                menuItem.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        String s = (String)JOptionPane.showInternalInputDialog(
                            frame.getContentPane(),
                            "Please enter the Port Number to use.",
                            "Sets Port number",JOptionPane.PLAIN_MESSAGE, icon,nullOptions,null);
                            //If a string was returned, say so.
                            if ((s != null) && (s.length() > 0)) {
                                setportNr(s);
                                return;
                            }
                    }
                });
        menu.add(menuItem);

        //Sets the IP of the server, our own IP
        menuItem = new JMenuItem("Set Server Ip Adress", KeyEvent.VK_0);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This sets the Ip adress of the server");
                menuItem.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        String s = (String)JOptionPane.showInternalInputDialog(
                            frame.getContentPane(),
                            "Please enter IPv4 adress of client.",
                            "Set Server Ip Dialog",JOptionPane.PLAIN_MESSAGE, icon,nullOptions,null);
                            //If a string was returned, say so.
                            if ((s != null) && (s.length() > 0)) {
                                setServerIpAdress(s);
                                output.append("Server Ip was set to:" + s + newline);
                                output.setCaretPosition(output.getDocument().getLength());
                                return;
                            }
                    }
                });
        menu.add(menuItem);

        // This opens a input window to write to the echo server. Echo server must be created before hand.
        menuItem = new JMenuItem("Echo Server", KeyEvent.VK_0);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This is a simple echo server running on the chosen port");
                menuItem.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        //Put functionality here
                    }
                });
        menu.add(menuItem);

        rbEchoServer = new JRadioButtonMenuItem("Activate Echo Server");
        rbEchoServer.setSelected(true);
        rbEchoServer.setMnemonic(KeyEvent.VK_R);
        rbEchoServer.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //Should check if all the settings are set before creating a thread.
                if(portNrSet){
                new Thread(() -> {
                    echoServer();
                }).start();
                    output.append("Echo server has been created" + newline);
                    output.setCaretPosition(output.getDocument().getLength());

                }
                
                else if(!portNrSet){
                        String s = "Error: The Port number is not set." + newline;
                       output.append(s + newline);
                       output.setCaretPosition(output.getDocument().getLength());
                        return;
                    }
                    else{
                        String s = "Error: Unknow error, this text should not be displayed." + newline;
                       output.append(s + newline);
                       output.setCaretPosition(output.getDocument().getLength());
                       return;
                    }
            }
        });
        menu.add(rbEchoServer);

        //Build the first menu.
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
            "General help information");
        menuBar.add(menu);

        menuItem = new JMenuItem("Username?", KeyEvent.VK_0);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This gets the chosen username");
                menuItem.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        String infoMessage = userName;
                        JOptionPane.showMessageDialog(
                            null, infoMessage, "InfoBox: " + "Username", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Port number?", KeyEvent.VK_0);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This gets the Port number that was set");
                menuItem.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        int infoMessage = portNr;
                        JOptionPane.showMessageDialog(
                            null, infoMessage, "InfoBox: " + "Port Number", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
        menu.add(menuItem);

        menuItem = new JMenuItem("Server Ip?", KeyEvent.VK_0);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This gets the Server Ip address that was set");
                menuItem.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        String infoMessage = serverIp;
                        JOptionPane.showMessageDialog(
                            null, infoMessage, "InfoBox: " + "Server Ip", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
        menu.add(menuItem);
        return menuBar;
    }

    public Container createContentPane() {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);

        //Create a scrolled text area.
        output = new JTextArea(5, 30);
        output.setEditable(false);
        scrollPane = new JScrollPane(output);

        //Add the text area to the content pane.
        contentPane.add(scrollPane, BorderLayout.CENTER);
        return contentPane;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Action event detected."
                   + newline
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")";
        output.append(s + newline);
        output.setCaretPosition(output.getDocument().getLength());
    }

    public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Item event detected."
                   + newline
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    New state: "
                   + ((e.getStateChange() == ItemEvent.SELECTED) ?
                     "selected":"unselected");
        output.append(s + newline);
        output.setCaretPosition(output.getDocument().getLength());
    }

    // Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Menu.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private void echoServerResponder(){

    }

    
    private void echoServer(){
            String threadname = Thread.currentThread().getName();
            output.append("Echo server threadname:"+threadname + newline);
            output.setCaretPosition(output.getDocument().getLength());
            
            try {
                //create server socket
                this.serverSocket =
                    new ServerSocket(this.portNr);
                control.EchoServerFlag = true;

            } catch (IOException e) {
                String s = "Exception caught when trying to listen on port:" + this.portNr + "or listening for a connection"
                 + newline + e.getMessage() + newline;
                output.append(s + newline);
                output.setCaretPosition(output.getDocument().getLength());
                this.setEchoServer = false;
                control.EchoServerFlag = false;
                return;
            }
                    /*
                     * Since accept bloks until someone connects we should create a new thread everytime someone connects.
                     * GUI will see all messages, should send messages back to all clients, like messenger.
                     * 
                     * we have to synchronize all access to output on GUI, otherwise we get race conditions
                     */
                    ExecutorService executor = Executors.newFixedThreadPool(5);
                    
                    //Everytime we get a new client request, accept returns a socket for communicating with that client.
                    try {

                    while((clientSocket = serverSocket.accept()) != null){

                        executor.execute(new Thread(() -> {
                            
                                //Code goes here
                                Socket workerSocket = clientSocket;
                                synchronized(this){
                                output.append("Accepted connection from client on thread:" + Thread.currentThread().getName() 
                                     + newline + "\t with socket name:" + workerSocket);   
                                output.setCaretPosition(output.getDocument().getLength());
                                }
                                //getInputStream -> InputStreamReader converts byte stream to char stream. BufferedReader is a buffer for storing char from stream
                                try {
                                    in = new BufferedReader(
                                    new InputStreamReader(workerSocket.getInputStream()));
                                out = new PrintStream(
                                    workerSocket.getOutputStream());
                                } catch (IOException e) {
                                    synchronized(this){
                                output.append("IOException caught in thread pool block while creating Bufferred Reader and Printstream: " + newline);
                                output.append(e.getMessage() + newline);
                                output.setCaretPosition(output.getDocument().getLength());
                                    }
                                }

                            

                                try {
                                String s;
                                while ((s = in.readLine()) != null) {
        
                                    if(s.equals("end")){
                                        System.out.println(s);
                                        synchronized(this){

                                        output.append("Client:" + workerSocket + "\t" + " disconnected" + newline);
                                        output.setCaretPosition(output.getDocument().getLength());
                                        }
                                        in.close();
                                        break;
                                    }
        
                                    else{
                                        //Write back to client, write clients message in GUI and flush output.
                                        //Får vi måske race condition når 2 clients prøver at skrive til serveren?
                                        
                                            out.println(s);

                                            synchronized(this){
                                            output.append(s + newline);
                                            output.setCaretPosition(output.getDocument().getLength());
                                            }

                                            out.flush();
                                        
                                        
                                    }
        
                                }

                                } catch (Exception e) {
                                    synchronized(this){
                                    output.append("Exception caught in thread pool block (in.ReadLine): " + newline);
                                    output.append(e.getMessage() + newline);
                                    output.setCaretPosition(output.getDocument().getLength());
                                    }
                                }

                                try {
                                    workerSocket.close();
                                } catch (Exception e) {
                                    synchronized(this){
                                    output.append("Exception caught in thread pool block while closing workerSocket: " + workerSocket + newline);
                                    output.append(e.getMessage() + newline);
                                    output.setCaretPosition(output.getDocument().getLength());
                                    }
                                }
                            }));
                       }

                       
                       executor.shutdown();
                       while(!executor.isTerminated()){ }
                       output.append("All threads have finished" + newline);   
                       output.setCaretPosition(output.getDocument().getLength());
                    

                    } catch (Exception e) {
                        output.append("Exception caught in thread pool block: "  + newline);
                        output.append(e.getMessage() + newline);
                        output.setCaretPosition(output.getDocument().getLength());
                    }

}
        

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MyMSN");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        Menu demo = new Menu();
        frame.setJMenuBar(demo.createMenuBar());
        frame.setContentPane(demo.createContentPane());
        demo.setFrame(frame);

        //Display the window.
        frame.setSize(450, 260);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
