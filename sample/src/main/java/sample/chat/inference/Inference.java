package sample.chat.inference;

import models.Entity;
import models.Intent;
import models.UserUtterance;
import models.component.RequirementStatus;

import java.util.List;
import java.util.Map;

/**
 * Model for an inferred user utterance
 *
 * @author Hok-Ming J. Zhong
 */
public class Inference {

    private Entity topic;
    private Map<Intent, RequirementStatus> intents;
    private List<Entity> namedEntities;
    private UserUtterance utterance;

    public Entity getTopic() {
        return topic;
    }

    public void setTopic(Entity topic) {
        this.topic = topic;
    }

    public Map<Intent, RequirementStatus> getIntents() {
        return intents;
    }

    public void setIntents(Map<Intent, RequirementStatus> intents) {
        this.intents = intents;
    }

    public List<Entity> getNamedEntities() {
        return namedEntities;
    }

    public void setNamedEntities(List<Entity> namedEntities) {
        this.namedEntities = namedEntities;
    }

    public UserUtterance getUtterance() {
        return utterance;
    }

    public void setUtterance(UserUtterance utterance) {
        this.utterance = utterance;
    }
}
