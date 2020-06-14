package multithreading;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection extends Thread {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objInp;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        objOut = new ObjectOutputStream(this.socket.getOutputStream());
        objInp = new ObjectInputStream(this.socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendChatMessage(ChatMessage msg) throws IOException {
        objOut.writeObject(msg);
        objOut.flush();
    }
    public ChatMessage readChatMessage() throws IOException, ClassNotFoundException {
            return  (ChatMessage) objInp.readObject();
    }

    public void closeAll(){
        try {
            socket.close();
            objInp.close();
            objOut.close();
        } catch (IOException e) {
            System.out.println("Подключение не закрылось");
        }

    }

}

