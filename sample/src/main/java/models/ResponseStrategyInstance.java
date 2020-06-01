package models;

import sample.chat.inference.Inference;

/**
 * Model of Response Strategy Instance table
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
public class ResponseStrategyInstance {
    private Integer id;
    private ResponseStrategy responseStrategy;
    private Intent intent;
    private TemplateText templateText;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResponseStrategy getResponseStrategy() {
        return responseStrategy;
    }

    public void setResponseStrategy(ResponseStrategy responseStrategy) {
        this.responseStrategy = responseStrategy;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public TemplateText getTemplateText() {
        return templateText;
    }

    public void setTemplateText(TemplateText templateText) {
        this.templateText = templateText;
    }

    public String getResponse(Inference inference) {
        if (responseStrategy == null || intent == null) return "";

        String response = responseStrategy.createResponse(intent, templateText, inference);

        return response;
    }
}
