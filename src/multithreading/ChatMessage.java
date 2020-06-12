package multithreading;

import java.io.Serializable;
import java.time.LocalTime;

public class ChatMessage implements Serializable {
    private String sender;
    private String text;
    private LocalTime time = LocalTime.now().withNano(0);

    public ChatMessage(String sender, String text) {
        this.text = text;
        this.sender = sender;
    }
    public String getText() {return text;}
    public void setText(String text) {this.text = text;}
    public LocalTime getTime() { return time; }
    public void setSender(String sender) {
        this.sender = sender;
    }
    @Override
    public String toString() {
        return  "[" + time + "] " + sender +  ": " + text;
    }
}

