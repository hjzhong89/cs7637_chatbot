package sample.chat;

import data.ChatBotDataSource;
import exception.BusinessLogicException;
import models.UserUtterance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import sample.chat.inference.Inference;
import sample.chat.inference.InferenceEngine;
import sample.chat.nlp.NaturalLanguageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialogue manager for chat application.
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.002
 */
@Component
public class DialogueManager {

    @Autowired
    @Qualifier("nlp")
    private NaturalLanguageProcessor processor;
    private List<Inference> history;

    @Autowired
    private ChatBotDataSource dataSource;

    public DialogueManager() throws BusinessLogicException {
        setHistory(new ArrayList<>());
    }

    void setHistory(List<Inference> history) {
        this.history = history;
    }

    public ChatBotDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(ChatBotDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> converse(String text) throws BusinessLogicException {
        ResponseManager rm = new ResponseManager(this.dataSource);
        List<String> responses = new ArrayList<>();
        List<UserUtterance> utterances = processor.parse(text);
        InferenceEngine ie = new InferenceEngine(dataSource);
        ie.setHistory(history);

        for (UserUtterance utterance : utterances) {
            Inference inference = ie.infer(utterance);
            history.add(inference);
            responses.addAll(rm.getResponses(inference));
        }

        return responses;
    }
}
