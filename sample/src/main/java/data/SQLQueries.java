package data;

/**
 * SQL queres used by ChatBotDataSource
 *
 * @author Hok-Ming J. Zhong
 */
public interface SQLQueries {
    String GET_INTENT_BY_ID = "SELECT ID, NAME FROM chatbot.INTENT WHERE ID = ?";
    String FIND_INTENTS_BY_KEY_WORDS =
            "SELECT DISTINCT INTENT.ID, INTENT.NAME," +
                    "INTENT_COMPONENT.COMPONENT_TYPE, INTENT_COMPONENT.COMPONENT " +
                    "FROM chatbot.INTENT " +
                    "LEFT OUTER JOIN chatbot.INTENT_COMPONENT " +
                    "ON INTENT_COMPONENT.INTENT_ID = INTENT.ID " +
                    "INNER JOIN chatbot.INTENT_KEY_WORD " +
                    "ON INTENT.ID = INTENT_KEY_WORD.INTENT_ID ";

    String FIND_RESPONSE_STRATEGY_INSTANCE_BY_INTENT =
            "SELECT RESPONSE_STRATEGY.ID AS RESPONSE_STRATEGY_ID, " +
                    "RESPONSE_STRATEGY.NAME AS RESPONSE_STRATEGY_NAME, " +
                    "INTENT.ID AS INTENT_ID, " +
                    "INTENT.NAME AS INTENT_NAME, " +
                    "TEMPLATE_TEXT.ID AS TEMPLATE_TEXT_ID, " +
                    "TEMPLATE_TEXT.TEXT AS TEXT " +
                    "FROM chatbot.RESPONSE_STRATEGY_INSTANCE RSI " +
                    "INNER JOIN chatbot.RESPONSE_STRATEGY " +
                    "ON RESPONSE_STRATEGY.ID = RSI.RESPONSE_STRATEGY_ID " +
                    "INNER JOIN chatbot.INTENT " +
                    "ON INTENT.ID = RSI.INTENT_ID " +
                    "LEFT OUTER JOIN chatbot.TEMPLATE_TEXT " +
                    "ON TEMPLATE_TEXT.ID = RSI.TEMPLATE_TEXT_ID " +
                    "WHERE RSI.INTENT_ID = ? " +
                    "AND RSI.ENTITY_ID IS NULL " +
                    "AND RSI.INTENT_STATUS = ?";

    String FIND_ASSIGNMENT_BY_NAME = "SELECT ID, NAME, DESCRIPTION, TYPE, DUE_DATE, URL" +
            " FROM chatbot.ENTITY WHERE NAME = ? AND TYPE = 'ASSIGNMENT'";

    String GET_ENTITIES = "SELECT NAME, ENTITY.ID, DESCRIPTION, TYPE, DUE_DATE, URL " +
            "FROM chatbot.ENTITY " +
            "LEFT OUTER JOIN chatbot.ENTITY_KEY_WORD " +
            "ON ENTITY_KEY_WORD.ENTITY_ID = ENTITY.ID ";

    String GET_CACHED_WIKI_SEARCH_BY_SUBJECT = "SELECT ID, SEARCH_SUBJECT, PAGE_TITLE, PAGE_TEXT " +
            "FROM chatbot.WIKI_SEARCH_CACHE WHERE SEARCH_SUBJECT LIKE ?";

    String UPDATE_CACHED_WIKI_SEARCH_BY_ID = "UPDATE chatbot.WIKI_SEARCH_CACHE " +
            "SET SEARCH_SUBJECT = ?, PAGE_TITLE = ?, PAGE_TEXT = ? WHERE ID = ? OR PAGE_TITLE = ?";

    String INSERT_CACHE_WIKI_SEARCH = "INSERT INTO chatbot.WIKI_SEARCH_CACHE " +
            "(SEARCH_SUBJECT, PAGE_TITLE, PAGE_TEXT) VALUES (?, ?, ?)";
}