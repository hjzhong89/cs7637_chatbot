package sample.chat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller for /chat
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
@Controller
public class ChatController {

    @ModelAttribute("module")
    String module() {
        return "chat";
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        model.addAttribute(new ChatInput());
        return "chat/chat";
    }

}
