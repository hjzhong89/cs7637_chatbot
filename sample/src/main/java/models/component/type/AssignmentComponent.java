package models.component.type;

import models.Entity;
import models.UserUtterance;
import models.component.RequirementStatus;
import models.component.properties.ComponentDescription;
import models.component.properties.ComponentDueDate;
import models.component.properties.ComponentName;

import java.util.Date;

/**
 * Assignment intent component
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
public class AssignmentComponent extends EntityComponent
        implements ComponentName, ComponentDueDate, ComponentDescription {
    @Override
    public String getType() {
        return "ASSIGNMENT";
    }

    @Override
    public String getName() {
        return getEntity() == null ? null : getEntity().getName();
    }

    @Override
    public Date getDueDate() {
        return getEntity() == null ? null : getEntity().getDueDate();
    }

    /**
     * Checks for assignments and stores the first one in assignment property
     *
     * @param topic Topic of the utterance
     * @return COMPLETE if there is at least one assignment with complete data
     */
    @Override
    public RequirementStatus getRequirementStatus(Entity topic, UserUtterance utterance) {
        if (topic == null) {
            return RequirementStatus.MISSING;
        }
        setEntity(topic);
        if (topic.getDescription() == null
                || topic.getDescription().isEmpty()
                || topic.getDueDate() == null) {
            return RequirementStatus.INCOMPLETE;
        }

        return RequirementStatus.COMPLETE;
    }
}
