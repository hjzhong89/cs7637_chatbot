package models;

import sample.chat.inference.Inference;

/**
 * The strategy used to generate a response
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
public abstract class ResponseStrategy {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract String createResponse(Intent intent, TemplateText templateText, Inference inferenceø);
}