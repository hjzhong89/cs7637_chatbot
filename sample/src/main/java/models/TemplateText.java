package models;

/**
 * Template of text used by a response strategy instance
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.06
 */
public class TemplateText {
    private Integer id;
    private String text;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
