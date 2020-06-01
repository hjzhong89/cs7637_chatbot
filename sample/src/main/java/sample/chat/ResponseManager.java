package sample.chat;

import data.ChatBotDataSource;
import exception.BusinessLogicException;
import exception.DataException;
import models.Intent;
import models.ResponseStrategyInstance;
import models.component.RequirementStatus;
import models.response.strategy.instance.WikiSearchResponse;
import sample.chat.inference.Inference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates and manages responses for a user utterance
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
class ResponseManager {

    private ChatBotDataSource ds;

    ResponseManager(ChatBotDataSource ds) {
        this.ds = ds;
    }

    List<String> getResponses(Inference inference) throws BusinessLogicException {
        List<String> responses = new ArrayList<>();
        Map<Intent, RequirementStatus> intentStatuses = inference.getIntents();

        try {
            for (Intent intent : intentStatuses.keySet()) {
                String status = intentStatuses.get(intent).toString();
                ResponseStrategyInstance rsi = ds.getResponseStrategyInstance(intent, status);

                if (rsi != null) {
                    if (rsi.getResponseStrategy() instanceof WikiSearchResponse) {
                        WikiSearchResponse wikiSearchResponse = (WikiSearchResponse) rsi.getResponseStrategy();
                        wikiSearchResponse.setDs(ds);
                    }
                    responses.add(rsi.getResponse(inference));
                }
            }

        } catch (DataException e) {
            e.printStackTrace();
            throw new BusinessLogicException(e.getMessage(), e);
        }

        if (responses.isEmpty()) {
            responses.add("I'm sorry. I didn't understand that.");
        }
        return responses;
    }
}
