import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientMessagesThread extends Thread{
    private BufferedReader reader;
    private PrintWriter writer;
    private Client client;

    public ClientMessagesThread(BufferedReader reader, PrintWriter writer, Client client) {
        this.reader = reader;
        this.writer = writer;
        this.client = client;
    }

    public void run() {
        String line;
        try {
            while(true) {
                line = reader.readLine();
                if(!line.equals("")) {
                    if(line.substring(0,3).equals("add")) { //using prefix "add" as command
                        client.clearuser(); //clear userlist
                        client.adduser(line.substring(3)); //add new user
                    }else {
                        client.getMessage(line);
                    }

                }
            }
        } catch(Exception ex) {

        }
    }
}
