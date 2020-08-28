import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
                client.send(null, clientname, "adduser");
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
            send(Username, "connected!", "servermessage");
        }

        public String getTime() {
            Calendar calendar = Calendar.getInstance();

            String Jahr = String.valueOf(calendar.get(Calendar.YEAR));
            String Monat = String.valueOf(calendar.get(Calendar.MONTH));
            String Tag = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String Stunde = String.valueOf(calendar.get(Calendar.HOUR));
            String Minute = String.valueOf(calendar.get(Calendar.MINUTE));

            if(Minute.length() < 2) {
                Minute = "0" + Minute;
            }

            return (Tag + ". " + Monat + " " + Jahr + "     " + Stunde + ":" + Minute);
        }

        public void send(String Username, String message, String typeofmessage){ //send everything to every client
            switch (typeofmessage){
                case "servermessage": writer.println("----------Servermessage:  " + Username + " " +  message + "----------");
                break;

                case "message": writer.println(Username + ": " + message);
                break;

                case "adduser": writer.println("add" + message);
                break;

                case "time": writer.println("Datum: " + getTime() + "\n" + "Du bist verbunden seit ");
                break;
            }

        }

        public void run()  {
            String line;
            Boolean running = true;
            try    {
                while(running)   {
                    line = reader.readLine().toLowerCase();
                    if (line.charAt(0) == '/') { //check if command
                        switch(line.substring(1).trim()) {
                            case "quit": //quitcommand called
                                clients.remove(this); //remove client from list
                                users.remove(Username); //remove name of client from list
                                send(Username,"disconnected!", "servermessage"); //send disconnect message
                                for(ClientHandler client: clients) {
                                    if(client.Username.equals(Username)) {
                                        reader.close(); //close reader
                                        writer.close(); //close writer
                                        this.client.close(); // close connection
                                        running = false; //kill thread
                                    }
                                }
                                break;

                            case "time": //time will be printed out
                                send(null, null ,"time");
                                break;
                        }

                    }

                    if(!line.equals("null")) {
                        send(Username,line, "message");
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


