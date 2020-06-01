package models;

import edu.stanford.nlp.trees.GrammaticalStructure;

import java.util.HashSet;
import java.util.Set;

/**
 * Tokenized values of the user's input and grammatical structure of sentences
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
public class UserUtterance {
    private String topic;
    private GrammaticalStructure gs;
    private Set<String> keyWords;
    private Set<String> namedEntities;

    public UserUtterance() {
        setKeyWords(new HashSet<>());
        setNamedEntities(new HashSet<>());
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public GrammaticalStructure getGrammaticalStructure() {
        return gs;
    }

    public void setGrammaticalStructure(GrammaticalStructure gs) {
        this.gs = gs;
    }

    public Set<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(Set<String> keyWords) {
        this.keyWords = keyWords;
    }

    public Set<String> getNamedEntities() {
        return namedEntities;
    }

    public void setNamedEntities(Set<String> namedEntities) {
        this.namedEntities = namedEntities;
    }
}
