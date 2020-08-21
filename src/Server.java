import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private int port;
    ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    ArrayList<String> users = new ArrayList<String>();

    public Server(int port) throws Exception{
        this.port = port;

        try {
            ServerSocket serverSocket = new ServerSocket(port); //server starting on port {port}
            System.out.println("Server started");

            while(true) {
                System.out.println("Looking for connection");
                Socket client = serverSocket.accept();//wait for client to connect
                System.out.println("Found a client");
                ClientHandler ch = new ClientHandler(client); //starting thread for client
                clients.add(ch);
                refreshUserList();
                ch.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshUserList() {
        for(ClientHandler client: clients) {
            for(String clientname: users) {
                client.send(null, clientname, 3);
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Server s = new Server(345);
    }

    class ClientHandler extends Thread{
        String Username;
        Socket client;
        BufferedReader reader;
        PrintWriter writer;

        public ClientHandler(Socket client) throws Exception{
            this.client = client;

            reader = new BufferedReader(new InputStreamReader( client.getInputStream())); //setting up reader
            writer = new PrintWriter (client.getOutputStream(),true); //setting up writer


            while(this.Username == null) {
                String name = reader.readLine();
                if(!users.contains(name) && !name.equals("null")) {
                    this.Username = name;
                    writer.println("true");
                }else {
                    writer.println("false");
                }
            }

            users.add(this.Username); //add to list of users
            sleep(100);
            send(Username, "connected!", 1);
        }



        public void send(String Username, String message, int typeofmessage){ //send everything to every client
            System.out.println("case" + typeofmessage);
            switch (typeofmessage){
                case 1: writer.println("----------Servermessage:  " + Username + " " +  message + "----------");
                break;

                case 2: writer.println(Username + ": " + message);
                break;

                case 3: writer.println("add" + message);
                break;
            }

        }

        public void run()  {
            String line;
            try    {
                while(true)   {
                    line = reader.readLine();
                    if (line.equals("end") ) { //close when "end" is received from client
                        clients.remove(this); //remove client from list
                        users.remove(Username); //remove name of client from list
                        send(Username,"disconnected!", 1);
                        break;
                    }

                    if(!line.equals("null")) {
                        send(Username,line, 2);
                    }
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());
            }
        }

    }


}


