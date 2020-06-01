package models.component.type;

import models.Entity;
import models.IntentComponent;
import models.UserUtterance;
import models.component.RequirementStatus;
import models.component.properties.ComponentDescription;
import models.component.properties.ComponentName;

/**
 * Intent component that for basic entities
 */
public class EntityComponent extends IntentComponent implements
        ComponentDescription, ComponentName {
    private Entity entity;
    private String property;

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String getName() {
        return entity == null ? null : entity.getName();
    }

    @Override
    public String getDescription() {
        return entity == null ? null : entity.getDescription();
    }

    @Override
    public RequirementStatus getRequirementStatus(Entity topic, UserUtterance utterance) {

        if (topic == null) {
            return RequirementStatus.ENTITY_MISSING;
        }
        setEntity(topic);

        switch (property) {
            case "DESCRIPTION":
                return (entity.getDescription() == null
                        || entity.getDescription().isEmpty()) ?
                        RequirementStatus.INCOMPLETE : RequirementStatus.COMPLETE;
            default:
                return RequirementStatus.COMPLETE;
        }
    }
}
