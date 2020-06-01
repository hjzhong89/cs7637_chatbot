package data;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import exception.DataException;
import models.*;
import models.component.type.AssignmentComponent;
import models.component.type.DependencyComponent;
import models.component.type.EntityComponent;
import models.response.strategy.instance.TemplateTextResponse;
import models.response.strategy.instance.WikiSearchResponse;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Chat Bot database
 *
 * @author Hok-Ming J. Zhong
 * @version 2016.11.001
 */
@Component
public class ChatBotDataSource implements SQLQueries {
    private MysqlDataSource ds;

    public ChatBotDataSource(MysqlDataSource ds) {
        this.ds = ds;
    }

    public Intent getIntentById(Integer id) throws DataException {
        Intent intent = new Intent();

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = getIntentByIdPS(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                intent.setId(rs.getInt("ID"));
                intent.setName(rs.getString("NAME"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }

        return intent;
    }

    public List<Intent> getIntentsByKeyWord(List<String> lemmas) throws DataException {

        List<Intent> intents = new ArrayList<>();
        if (lemmas.isEmpty()) {
            return intents;
        }
        Integer currentIntentId = null;
        Intent intent = null;
        try (
                Connection conn = ds.getConnection();
                PreparedStatement ps = createGetIntentsPS(conn, lemmas);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Integer id = rs.getInt("ID");
                if (id != currentIntentId) {
                    intent = new Intent();
                    intent.setId(id);
                    intent.setName(rs.getString("NAME"));
                    intents.add(intent);
                }
                String componentType = rs.getString("COMPONENT_TYPE");
                String componentValue = rs.getString("COMPONENT");
                currentIntentId = id;
                if (componentType == null) {
                    continue;
                }
                switch (componentType) {
                    case "ENTITY":
                        EntityComponent ec = new EntityComponent();
                        ec.setProperty(componentValue);
                        intent.getComponents().add(ec);
                        break;
                    case "ASSIGNMENT":
                        AssignmentComponent ac = new AssignmentComponent();
                        intent.getComponents().add(ac);
                        break;
                    case "DEPENDENCY":
                        DependencyComponent dc = new DependencyComponent();
                        dc.setDependencyStructure(componentValue);
                        intent.getComponents().add(dc);
                    default:
                        break;
                }
            }

        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
        return intents;
    }

    public List<Entity> getEntities(List<String> namedEntities) throws DataException {
        List<Entity> entities = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = createGetEntityPs(conn, namedEntities);
             ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Entity entity = new Entity();
                entity.setId(rs.getInt("ID"));
                entity.setName(rs.getString("NAME"));
                entity.setDescription(rs.getString("DESCRIPTION"));
                entity.setType(rs.getString("TYPE"));
                entity.setDueDate(rs.getDate("DUE_DATE"));
                entity.setUrl(rs.getString("URL"));
                entities.add(entity);
            }
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }

        return entities;
    }

    public ResponseStrategyInstance getResponseStrategyInstance(Intent intent, String intentStatus) throws DataException {
        ResponseStrategyInstance rsi = new ResponseStrategyInstance();
        try (
                Connection conn = ds.getConnection();
                PreparedStatement ps = createGetResponseStrategyPS(conn, intent, intentStatus);
                ResultSet rs = ps.executeQuery()
        ) {
            if (!rs.next()) {
                return null;
            }
            ResponseStrategy responseStrategy;
            String responseStrategyName = rs.getString("RESPONSE_STRATEGY_NAME");
            switch (responseStrategyName) {
                case "TEMPLATE_TEXT_RESPONSE":
                    responseStrategy = new TemplateTextResponse();
                    break;
                case "WIKI_SEARCH":
                    responseStrategy = new WikiSearchResponse();
                    break;
                default:
                    return null;
            }

            responseStrategy.setId(rs.getInt("RESPONSE_STRATEGY_ID"));
            responseStrategy.setName(responseStrategyName);

            TemplateText templateText = new TemplateText();
            templateText.setId(rs.getInt("TEMPLATE_TEXT_ID"));
            templateText.setText(rs.getString("TEXT"));

            rsi.setResponseStrategy(responseStrategy);
            rsi.setTemplateText(templateText);
            rsi.setIntent(intent);

        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }

        return rsi;
    }

    public Entity getAssignmentByName(String name) throws DataException {
        Entity assignment = new Entity();
        assignment.setName(name);
        assignment.setType("ASSIGNMENT");
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = getAssignmentByNamePS(conn, name);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                assignment.setId(rs.getInt("ID"));
                assignment.setName(rs.getString("NAME"));
                assignment.setDescription(rs.getString("DESCRIPTION"));
                assignment.setType(rs.getString("TYPE"));
                assignment.setDueDate(rs.getDate("DUE_DATE"));
                assignment.setUrl(rs.getString("URL"));
            }
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }

        return assignment;
    }

    public CachedWikiSearch getCachedWikiSearchBySubject(String subject) throws DataException {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = createGetCachedWikiSearchPS(conn, subject);
             ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                CachedWikiSearch result = new CachedWikiSearch();
                result.setId(rs.getInt("ID"));
                result.setSearchSubject(rs.getString("SEARCH_SUBJECT"));
                result.setPageTitle(rs.getString("PAGE_TITLE"));
                result.setPageText(rs.getString("PAGE_TEXT"));
                return result;
            }
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
        return null;
    }

    public void cacheWikiSearch(String subject, String pageTitle, String description) throws DataException {
        CachedWikiSearch result = getCachedWikiSearchBySubject(subject);

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = result == null ? getInsertWikiSearchCachePS(conn, subject, pageTitle, description)
                     : getUpdateWikiSearchCachePS(conn, result, subject, pageTitle, description)
        ) {
            ps.execute();
        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    /*--------------------------------------------------*/
    /*          Prepared Statements                     */
    /*__________________________________________________*/

    private PreparedStatement createGetIntentsPS(Connection conn, List<String> lemmas)
            throws SQLException {
        String where = "WHERE INTENT_KEY_WORD.KEY_WORD IN (?";
        String orderBy = "ORDER BY INTENT.ID DESC";
        for (int i = 0; i < lemmas.size() - 1; i++) {
            where += ",?";
        }
        where += ") ";
        String sql = FIND_INTENTS_BY_KEY_WORDS + where + orderBy;
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 1; i <= lemmas.size(); i++) {
            ps.setString(i, lemmas.get(i - 1));
        }

        return ps;
    }

    private PreparedStatement createGetResponseStrategyPS(Connection conn, Intent intent, String intentStatus)
            throws SQLException {
        String sql = FIND_RESPONSE_STRATEGY_INSTANCE_BY_INTENT;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, intent.getId());
        ps.setString(2, intentStatus);
        return ps;
    }

    private PreparedStatement getIntentByIdPS(Connection conn, Integer id) throws SQLException {
        String sql = GET_INTENT_BY_ID;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;

    }

    private PreparedStatement getAssignmentByNamePS(Connection conn, String name) throws SQLException {
        String sql = FIND_ASSIGNMENT_BY_NAME;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        return ps;
    }

    private PreparedStatement createGetEntityPs(Connection conn, List<String> namedEntities) throws SQLException {
        String getEntities = GET_ENTITIES;
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (int i = 1; i < namedEntities.size(); i++) {
            sb.append("?,");
        }

        sb.append("?)");

        String inKeyWord = sb.toString();
        String keyWordClause = "WHERE ENTITY_KEY_WORD.KEY_WORD IN " + inKeyWord;
        String entityNameClause = " OR ENTITY.NAME IN " + inKeyWord;
        String sql = getEntities + keyWordClause + entityNameClause;

        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < namedEntities.size(); i++) {
            ps.setString(i + 1, namedEntities.get(i).trim());
            ps.setString(i + 1 + namedEntities.size(), namedEntities.get(i).trim());
        }

        return ps;
    }

    private PreparedStatement createGetCachedWikiSearchPS(Connection conn, String subject) throws SQLException {
        String sql = GET_CACHED_WIKI_SEARCH_BY_SUBJECT;
        PreparedStatement ps = conn.prepareStatement(sql);
        subject = "%" + subject.toUpperCase() + "%";
        ps.setString(1, subject);
        return ps;
    }

    private PreparedStatement getInsertWikiSearchCachePS(Connection conn, String subject, String pageTitle, String description) throws SQLException {
        String sql = INSERT_CACHE_WIKI_SEARCH;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, subject);
        ps.setString(2, pageTitle);
        ps.setString(3, description);
        return ps;
    }

    private PreparedStatement getUpdateWikiSearchCachePS(Connection conn, CachedWikiSearch result, String subject, String pageTitle, String description) throws SQLException {
        String sql = UPDATE_CACHED_WIKI_SEARCH_BY_ID;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, subject);
        String pt;
        String desc;

        if (pageTitle != null && !pageTitle.equals(result.getPageTitle())) {
            pt = pageTitle;
            desc = description;
        } else {
            pt = result.getPageTitle();
            if (result.getPageText() == null || result.getPageText().isEmpty()) {
                desc = description;
            } else {
                desc = result.getPageText();
            }
        }

        ps.setString(2, pt);
        ps.setString(3, desc);
        ps.setInt(4, result.getId());
        ps.setString(5, pt);
        return ps;
    }
}
