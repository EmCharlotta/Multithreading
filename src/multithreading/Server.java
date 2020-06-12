package multithreading;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private int port;
    public ArrayBlockingQueue<ChatMessage> msgList;
    private static Set<Connection> connections = Collections.synchronizedSet(new HashSet<>());

    public Server(int port) {
        this.port = port;
        this.msgList = new ArrayBlockingQueue<ChatMessage>(10, true);
        ServerWriter writer = new ServerWriter(this);
        writer.start();
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
            Set<Connection> connections = server.getConnections();
            System.out.println(connections);
            Iterator<Connection> iterator = connections.iterator();
            ChatMessage chatMessage = null;
            try {
                chatMessage = server.getMsgList().take();
                while (iterator.hasNext()) {
                    Connection con = iterator.next();
                    con.sendChatMessage(chatMessage);
                    iterator.remove();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

//        while (true) {
//                try {
//                    chatMessage = (ChatMessage)server.getMsgList().take();
//                while (connectionIterator.hasNext()) {
//                        connectionIterator.next().sendChatMessage(chatMessage);
//                                   }
//                } catch (InterruptedException |IOException e) {
//                    e.printStackTrace();
//            }
//        }

    class ServerReader extends Thread {
        Connection connection;
        Server server;

        public ServerReader(Connection connection, Server server) {
            this.connection = connection;
            this.server = server;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ChatMessage chatMessage = connection.readChatMessage();
                    server.getMsgList().put(chatMessage);
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

