package models.component.type;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import models.Entity;
import models.IntentComponent;
import models.UserUtterance;
import models.component.RequirementStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dependency on grammatical structure of sentences.
 */
public class DependencyComponent extends IntentComponent {
    private static final String DEPENDENCY_PATTERN = "<<[\\w\\.:]*>>";
    private String dependencyStructure;

    public String getDependencyStructure() {
        return dependencyStructure;
    }

    public void setDependencyStructure(String dependencyStructure) {
        this.dependencyStructure = dependencyStructure;
    }

    @Override
    public String getType() {
        return "DEPENDENCY";
    }

    @Override
    public RequirementStatus getRequirementStatus(Entity topic, UserUtterance utterance) {

        if (dependencyStructure == null || dependencyStructure.isEmpty()) {
            return RequirementStatus.COMPLETE;
        }

        Pattern pattern = Pattern.compile(DEPENDENCY_PATTERN);
        Matcher matcher = pattern.matcher(dependencyStructure);
        List<Relationship> relationships = new ArrayList<>();

        while (matcher.find()) {
            Relationship relationship = new Relationship(topic, utterance.getGrammaticalStructure());
            Integer start = matcher.start() + 2;
            Integer end = matcher.end() - 2;
            String dependency = dependencyStructure.substring(start, end);
            String[] tokens = dependency.split(":");
            relationship.rel = tokens[0];
            relationship.value = tokens.length > 1 ? tokens[1] : null;
            relationships.add(relationship);
        }
        RequirementStatus hasAllRelationships = RequirementStatus.COMPLETE;
        GrammaticalStructure gs = utterance.getGrammaticalStructure();

        for (Relationship r : relationships) {
            RequirementStatus relationshipFound = RequirementStatus.COMPLETE;

            search:
            for (TypedDependency typedDependency : gs.allTypedDependencies()) {
                switch (r.relationshipFound(gs, typedDependency)) {
                    case ENTITY_MISSING:
                        relationshipFound = RequirementStatus.ENTITY_MISSING;
                        break search;
                    case INCOMPLETE:
                        relationshipFound = RequirementStatus.INCOMPLETE;
                        break search;
                    case MISSING:
                        relationshipFound = RequirementStatus.MISSING;
                        break;
                    case COMPLETE:
                        relationshipFound = RequirementStatus.COMPLETE;
                        break search;
                    default:
                        break;
                }
            }

            switch (relationshipFound) {
                case MISSING:
                    hasAllRelationships = RequirementStatus.MISSING;
                    break;
                case INCOMPLETE:
                    if (hasAllRelationships != RequirementStatus.MISSING) {
                        hasAllRelationships = RequirementStatus.INCOMPLETE;
                    }
                    break;
                case ENTITY_MISSING:
                    if (hasAllRelationships != RequirementStatus.MISSING) {
                        hasAllRelationships = RequirementStatus.ENTITY_MISSING;
                    }
                default:
                    break;
            }
        }


        return hasAllRelationships;
    }

    private class Relationship {
        Entity topic;
        GrammaticalStructure dependency;
        String rel;
        String value;

        Relationship(Entity topic, GrammaticalStructure dependency) {
            this.topic = topic;
            this.dependency = dependency;
        }

        RequirementStatus relationshipFound(GrammaticalStructure gs, TypedDependency typedDependency) {
            String relation = typedDependency.reln().getShortName().toUpperCase();
            String value = typedDependency.dep().get(CoreAnnotations.TextAnnotation.class).toUpperCase();

            if (!rel.equals(relation)) {
                return RequirementStatus.MISSING;
            }

            switch (rel) {
                case "NMOD":
                case "NSUBJ":
                    switch (this.value) {
                        case "ENTITY.NAME":
                            if (topic == null) {
                                return RequirementStatus.ENTITY_MISSING;
                            }

                            if (topic.getName() == null
                                    || topic.getName().isEmpty()
                                    || topic.getDescription() == null
                                    || topic.getDescription().isEmpty()) {
                                return RequirementStatus.INCOMPLETE;
                            }
                            return RequirementStatus.COMPLETE;
                        default:
                            break;
                    }
                default:
                    if (this.value != null) {
                        if (!this.value.equals(value)) {
                            return RequirementStatus.MISSING;
                        }
                    }
                    break;
            }

            return RequirementStatus.COMPLETE;
        }
    }
}
