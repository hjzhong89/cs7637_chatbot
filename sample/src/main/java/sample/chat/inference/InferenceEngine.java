package sample.chat.inference;

import data.ChatBotDataSource;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import exception.BusinessLogicException;
import exception.DataException;
import models.Entity;
import models.Intent;
import models.IntentComponent;
import models.UserUtterance;
import models.component.RequirementStatus;

import java.util.*;

/**
 * Class to take tokenized user utterance and extract intents, entities,
 * and any other input needed for the ResponseManager to create a response.
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
public class InferenceEngine {
    private ChatBotDataSource ds;
    private List<Inference> history;

    public InferenceEngine(ChatBotDataSource ds) {
        setDs(ds);
    }

    private void setDs(ChatBotDataSource ds) {
        this.ds = ds;
    }

    public void setHistory(List<Inference> history) {
        this.history = history;
    }

    public Inference infer(UserUtterance utterance) throws BusinessLogicException {
        Inference inference = new Inference();

        Map<Intent, RequirementStatus> statuses = new LinkedHashMap<>();
        List<String> keyWords = new ArrayList<>();
        List<String> namedEntities = new ArrayList<>();
        List<Intent> intents = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();

        keyWords.addAll(utterance.getKeyWords());
        namedEntities.addAll(utterance.getNamedEntities());

        try {
            // Get Intents
            if (!keyWords.isEmpty()) {
                intents = ds.getIntentsByKeyWord(keyWords);
            }

            // Get Entities from DB
            if (!namedEntities.isEmpty()) {
                entities = ds.getEntities(namedEntities);
            }

            /*
            Determine the topic of the intent and then determine if the requirements
            to fulfill the intent have been met. Each utterance should only have one
            fulfilled intent with a topic.
             */
            for (Intent intent : intents) {

                Entity topic;
                switch (intent.getName()) {
                    case "ASK_ABOUT":
                        topic = getTopic(utterance.getGrammaticalStructure(), "nmod");
                        break;
                    case "ASK_DESCRIPTION":
                        topic = getTopic(utterance.getGrammaticalStructure(), "nsubj");
                        break;
                    default:
                        if (entities.size() > 0) {
                            topic = entities.get(0);
                        } else {
                            topic = getTopic(utterance.getGrammaticalStructure(), "nsubj");
                        }
                }

                RequirementStatus status = checkDependencies(intent, topic, utterance);

                // Only get response for "MISSING" intents if it is the only intent found
                if (intents.size() <= 1) {
                    statuses.put(intent, status);
                    inference.setTopic(topic);
                } else {
                    // Only allow the most complete intents.

                }

                if ((intents.size() > 1 && status != RequirementStatus.MISSING)
                        || intents.size() == 1) {
                    statuses.put(intent, status);
                    inference.setTopic(topic);
                }
            }
        } catch (DataException e) {
            e.printStackTrace();
            throw new BusinessLogicException(e.getMessage(), e);
        }

        inference.setUtterance(utterance);
        inference.setIntents(statuses);
        inference.setNamedEntities(entities);

        return inference;
    }

    private RequirementStatus checkDependencies(Intent intent, Entity topic, UserUtterance utterance) {

        if (intent.getComponents().isEmpty()) {
            return RequirementStatus.COMPLETE;
        }

        RequirementStatus status = RequirementStatus.COMPLETE;

        for (IntentComponent component : intent.getComponents()) {
            RequirementStatus componentStatus = component.getRequirementStatus(topic, utterance);
            switch (componentStatus) {
                case MISSING:
                    if (status == RequirementStatus.COMPLETE)
                        status = RequirementStatus.MISSING;
                    break;
                case ENTITY_MISSING:
                    if (status == RequirementStatus.COMPLETE)
                        status = RequirementStatus.ENTITY_MISSING;
                    break;
                case INCOMPLETE:
                    if (status == RequirementStatus.COMPLETE)
                        status = RequirementStatus.INCOMPLETE;
                    break;
                default:
                    break;
            }
        }
        return status;
    }

    private Entity getTopic(GrammaticalStructure gs, String relation) throws DataException {
        Collection<TypedDependency> dependencies = gs.allTypedDependencies();
        StringBuilder sb = new StringBuilder();
        IndexedWord topic = null;

        // Cycle once to find the root of the topic
        for (TypedDependency dependency : dependencies) {
            if (dependency.reln().getShortName().equals(relation)) {
                topic = dependency.dep();
                break;
            }
        }

        if (topic != null) {
            if (topic.lemma().equals("it")) {
                if (history.isEmpty()) return null;
                return history.get(history.size() - 1).getTopic();
            }
            // Cycle again to extract full topic
            for (TypedDependency dependency : dependencies) {
                if (dependency.reln().getShortName().equals(relation) ||
                        (dependency.gov().equals(topic)
                                && !dependency.reln().getShortName().equals("case"))) {
                    if (dependency.dep().lemma().equals("its")) {
                        if (history.isEmpty()) return null;
                        return history.get(history.size() - 1).getTopic();
                    }
                    sb.append(dependency.dep().lemma());
                    sb.append(" ");
                }
            }
        }

        List<Entity> entities = ds.getEntities(Collections.singletonList(sb.toString()));
        if (entities.isEmpty()) {
            Entity subject = new Entity();
            subject.setName(sb.toString());
            return subject;
        } else {
            return entities.get(0);
        }
    }
}
