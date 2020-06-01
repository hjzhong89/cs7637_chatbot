package sample.chat;

import exception.BusinessLogicException;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * REST service for conversation
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.002
 */
@RestController
public class ChatService {

    @Inject
    private DialogueManager dm;

    @ResponseBody
    @RequestMapping(value = "/api/chat", method = RequestMethod.POST)
    public ChatResponse chat(@RequestBody ChatInput chatInput) {
        ChatResponse response = new ChatResponse();
        if (chatInput == null) {
            return null;
        }

        try {
            response.setResponses(dm.converse(chatInput.getText()));
        } catch (BusinessLogicException e) {
            e.printStackTrace();
            response.getResponses().add("An error occurred while processing your message.");
            response.getResponses().add(e.getMessage());
        }

        return response;
    }
}
