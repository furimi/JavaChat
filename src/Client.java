import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class Client extends JFrame{
    private ArrayList<String> users = new ArrayList<String>();
    private int port;
    private Socket client;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private JTextArea areatext;
    private JTextArea areauser;

    public Client( int port) throws Exception{
        this.port = port;

        client = new Socket("localhost", port); //connecting to server

        reader = new BufferedReader(new InputStreamReader(client.getInputStream())); //setting up reader
        writer = new PrintWriter(client.getOutputStream(),true); //setting up writer

        while(this.username == null) {
            String name = JOptionPane.showInputDialog("Gib deinen Namen ein!");
            writer.println(name);
            String input = reader.readLine(); // read input from server
            if(input.equals("true")) { // if server allows name, set name to username
                this.username = name;
            } else { // if name is not allowed, show error and ask for new name
                JOptionPane.showMessageDialog(null,"Der Name ist bereits vergeben!", "Fehler",JOptionPane.ERROR_MESSAGE);
            }
        }

        new ClientMessagesThread(reader, writer, this).start(); //start thread, listening for messages

        initialize(); // initialize frame
    }

    public void clearuser() { //clear list of users
        users.clear();
    }

    public void adduser(String usertoadd) { //add user to list
        users.add(usertoadd);
        refreshlist();
    }

    private void refreshlist() { //refresh shown list
        for(String user: users) {
            areauser.append(user + "\n");
        }
    }

    private void initialize() throws Exception{

        setLocationRelativeTo(null);
        setSize(1000,700);
        setResizable(false);
        setLayout(null);

        JTextField fieldinput = new JTextField();
        fieldinput.setBounds(50,600,500,25);
        fieldinput.requestFocus();

        JButton btnsend = new JButton("Senden");
        btnsend.setBounds(600,600,125,25);
        btnsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fieldinput.getText().equals("")) {
                    writer.println(fieldinput.getText());
                    fieldinput.setText("");
                }
            }
        });

        KeyListener tfKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER)
                    btnsend.doClick();
            }
        };

        fieldinput.addKeyListener(tfKeyListener);

        areatext = new JTextArea(10,20);
        areatext.setLineWrap(true);
        areatext.setBounds(5,25,650,500);
        areatext.setEditable(false);
        areatext.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(areatext, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        areauser = new JTextArea(10,20);
        areauser.setLineWrap(true);
        areauser.setBounds(675,25,250,500);
        areauser.setEditable(false);
        areauser.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll2 = new JScrollPane(areauser, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(areauser);
        add(scroll);
        add(btnsend);
        add(areatext);
        add(fieldinput);
        setVisible(true);

        clearuser();
        refreshlist();
    }

    public void getMessage(String message){
        areatext.append(message + "\n");
    }

    public static void main(String[] args) throws Exception{
        Client c = new Client(345);
    }
}
