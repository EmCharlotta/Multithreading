package multithreading;

public class Client2 {
    public static void main(String[] args) {

        Client client2 = new Client("127.0.0.1",8090);
        client2.startSending();

    }
}
