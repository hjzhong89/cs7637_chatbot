package sample.chat.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.util.CoreMap;
import exception.BusinessLogicException;
import models.UserUtterance;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenize text for inference engine to interpret
 *
 * @author Hok-Ming J. Zhong
 */
@Component
public class NaturalLanguageProcessor {

    @Autowired
    private StanfordCoreNLP pipeline;
    @Autowired
    private DependencyParser dependencyParser;
    @Autowired
    private Dictionary dictionary;

    public void setPipeline(StanfordCoreNLP pipeline) {
        this.pipeline = pipeline;
    }

    public void setDependencyParser(DependencyParser dependencyParser) {
        this.dependencyParser = dependencyParser;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public List<UserUtterance> parse(String text) throws BusinessLogicException {
        List<UserUtterance> utterances = new ArrayList<>();

        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);


        for (CoreMap sentence : sentences) {
            UserUtterance utterance = new UserUtterance();
            GrammaticalStructure gs = dependencyParser.predict(sentence);
            utterance.setGrammaticalStructure(gs);

            String previousNeTag = "O";
            String namedEntity = "";

            try {
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                    IndexWord indexWord = null;

                    if (!previousNeTag.equals(ne)) {
                        if (!namedEntity.isEmpty()) {
                            utterance.getNamedEntities().add(namedEntity.trim());
                        }
                        namedEntity = "";
                    }

                    previousNeTag = ne;

                    if (!"O".equals(ne)) {
                        namedEntity += " " + word;
                    } else {
                        // Get related lemmas. Only for non-named entities
                        switch (pos) {
                            case "NN":
                            case "NNS":
                            case "UH":
                                indexWord = dictionary.lookupIndexWord(POS.NOUN, word);
                                break;
                            case "JJ":
                                indexWord = dictionary.lookupIndexWord(POS.ADJECTIVE, word);
                                break;
                            case "VBZ":
                                indexWord = dictionary.lookupIndexWord(POS.VERB, word);
                                break;
                            case "PRP":
                                if ("YOU".equals(word.toUpperCase()) || "YOURSELF".equals(word.toUpperCase())) {
                                    utterance.getNamedEntities().add(word);
                                }
                                break;
                            default:
                                utterance.getKeyWords().add(word);
                                break;
                        }

                        if (indexWord != null) {
                            utterance.getKeyWords().add(indexWord.getLemma());
                            for (Synset synset : indexWord.getSenses()) {
                                synset.getWords().forEach(
                                        w -> utterance.getKeyWords().add(w.getLemma())
                                );
                            }
                        }
                    }


                }
            } catch (JWNLException e) {
                throw new BusinessLogicException(e.getMessage(), e);
            }
            if (!namedEntity.isEmpty()) {
                utterance.getNamedEntities().add(namedEntity);
            }
            utterances.add(utterance);
        }

        return utterances;
    }
}
