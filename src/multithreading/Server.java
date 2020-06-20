package multithreading;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {
    private int port;
    public ArrayBlockingQueue<ChatMessage> msgList;
    public static Set<Connection> connections = Collections.synchronizedSet(new HashSet<>());

    public Server(int port) {
        this.port = port;
        this.msgList = new ArrayBlockingQueue<ChatMessage>(10, true);
        ServerWriter serverWriter = new ServerWriter(this);
        serverWriter.start();
    }


    public Set<Connection> getConnections() {
        return connections;
    }

    public ArrayBlockingQueue<ChatMessage> getMsgList() {
        return msgList;
    }

    public void listen() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Сервер запущен");
        while (true) {
            Socket clientSocket = serverSocket.accept();
                Connection con = getConnection(clientSocket);
                connections.add(con);
                ServerReader reader = new ServerReader(con,this);
                reader.start();
        }
        }

    public Connection getConnection(Socket clientSocket) throws IOException {
        return new Connection(clientSocket);
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Server server = new Server(8090);
        server.listen();
    }
}
class ServerWriter extends Thread {
    Server server;

    public ServerWriter(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            ChatMessage chatMessage = null;
            try {
                chatMessage = server.getMsgList().take();
                Set<Connection> connections = server.getConnections();
                Iterator<Connection> iterator = connections.iterator();
                while (iterator.hasNext()) {
                    Connection con = iterator.next();
                    if (!chatMessage.equals(con.getMessage())) {
                        con.sendChatMessage(chatMessage);
                    }
                }
            } catch (InterruptedException | IOException e) {
                System.out.println("Ошибка в строчке 75");
            }
        }
    }
}

    class ServerReader extends Thread {
        Connection connection;
        Server server;

        public ServerReader(Connection connection, Server server) {
            this.connection = connection;
            this.server = server;
        }

        @Override
        public void run() {
            System.out.println("Новый поток для чтения клиента запущен");
            while (!connection.getMessage().equals("has left the chat")) {
                try {
                    ChatMessage chatMessage = connection.readChatMessage();
                    connection.setMessage(chatMessage);
                    if(chatMessage.getText().equalsIgnoreCase( "exit")){
                        connection.closeAll();
                        server.getConnections().remove(connection);
                        System.out.println(chatMessage.getSender() + " отключился");
                        chatMessage.setText("has left the chat");
                        server.getMsgList().put(chatMessage);
                        Thread.currentThread().interrupt();
                    }
                    else server.getMsgList().put(chatMessage);
                } catch (InterruptedException | IOException | ClassNotFoundException e) {
                    System.out.println("Ошибка в строчке 107");
                }
            }
        }
    }

