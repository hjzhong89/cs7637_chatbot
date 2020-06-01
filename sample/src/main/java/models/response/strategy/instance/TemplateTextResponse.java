package models.response.strategy.instance;

import models.Entity;
import models.Intent;
import models.ResponseStrategy;
import models.TemplateText;
import sample.chat.inference.Inference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Response strategy that uses only template text
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.002
 */
public class TemplateTextResponse extends ResponseStrategy {

    private static final String ENTITY_PATTERN = "<<[\\w\\.]*>>";

    @Override
    public String createResponse(Intent intent, TemplateText templateText, Inference inference) {
        String text = templateText.getText();
        Pattern pattern = Pattern.compile(ENTITY_PATTERN);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String token = templateText.getText().substring(matcher.start(), matcher.end());
            String value = replaceToken(inference.getTopic(), token);
            text = text.replace(token, value);
        }

        return text;
    }

    private String replaceToken(Entity topic, String token) {
        String value = "";

        switch (token) {
            case "<<ENTITY.NAME>>":
                value = topic.getName() == null ? "that" : topic.getName();
                break;
            case "<<ENTITY.DESCRIPTION>>":
                value = topic.getDescription() == null ? "that" : topic.getDescription();
                break;
            case "<<ENTITY.DUE_DATE>>":
                value = topic.getDueDate().toString();
                break;
            default:
                break;
        }

        return value;
    }
}
