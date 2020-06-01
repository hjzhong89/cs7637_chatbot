package models;

import models.component.RequirementStatus;

/**
 * Intent Component Model
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
public abstract class IntentComponent {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String tydpe) {
        this.type = type;
    }

    public abstract RequirementStatus getRequirementStatus(Entity topic, UserUtterance utterance);
}
