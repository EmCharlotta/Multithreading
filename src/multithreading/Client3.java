package multithreading;

public class Client3 {
    public static void main(String[] args) {

        Client client3 = new Client("127.0.0.1",8090);
        client3.startSending();

    }
}
