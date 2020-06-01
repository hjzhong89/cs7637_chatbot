package sample.chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaimezhong on 11/17/16.
 */
public class ChatResponse {
    private List<String> responses;

    public ChatResponse() {
        responses = new ArrayList<>();
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }
}
