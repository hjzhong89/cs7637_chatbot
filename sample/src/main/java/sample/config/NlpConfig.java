package sample.config;

import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sample.chat.nlp.NaturalLanguageProcessor;

import java.util.Properties;

/**
 * Creates the beans for NLP.
 */
@Configuration
class NlpConfig {

    @Bean(name = "nlp")
    public NaturalLanguageProcessor nlp() {
        NaturalLanguageProcessor nlp = new NaturalLanguageProcessor();
        nlp.setDictionary(dictionary());
        nlp.setDependencyParser(dependencyParser());
        nlp.setPipeline(pipeline());
        return nlp;
    }

    @Bean
    public StanfordCoreNLP pipeline() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
        props.put("regexner.mapping", "regexner/regexner-assignment.txt");
        return new StanfordCoreNLP(props);
    }

    @Bean
    public DependencyParser dependencyParser() {
        return DependencyParser.loadFromModelFile(DependencyParser.DEFAULT_MODEL);
    }

    @Bean
    public Dictionary dictionary() {
        Dictionary dictionary = null;
        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return dictionary;
    }
}
