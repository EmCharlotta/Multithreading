package multithreading;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String ip;
    private int port;
    private Scanner scan;
    public  Connection connection;
    private String name;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        scan = new Scanner(System.in);
        try {
            connection = new Connection(getSocket());
        } catch (IOException e) {
            connection.closeAll();
            System.out.println("Подключение не удалось");
        }
        System.out.println("Введите свой ник: ");
        String name = scan.nextLine();
        setName(name);
        ClientReading clientReading = new ClientReading(this);
        clientReading.start();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Socket getSocket() throws IOException {
        Socket socket = new Socket(ip, port);
        return socket;
    }

    public void startSending() {

        ChatMessage chatMessage = new ChatMessage(name, "");
        while (!chatMessage.getText().equals("exit")) {
            System.out.println("Введите сообщение:");
            String msg = scan.nextLine();
            chatMessage = new ChatMessage(name, msg);
            if(chatMessage.getText().equals("exit")){
                chatMessage = new ChatMessage(name, name + "has left the chat");
                try {
                    connection.sendChatMessage(chatMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connection.closeAll();
            }
                else try {
                    connection.sendChatMessage(chatMessage);
                } catch (IOException e) {
                    connection.closeAll();
                    System.out.println("Проблемы с подключением, не удается послать сообщение");
                }
            }
        }


    public static void main(String[] args) {

        Client client1 = new Client("127.0.0.1",8090);
        client1.startSending();
    }

}
class ClientReading extends Thread {
    private Client client;
    public ClientReading(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

            try {
                System.out.println(client.getConnection().readChatMessage());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        }
    }

