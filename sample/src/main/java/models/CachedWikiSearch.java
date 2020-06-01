package models;

/**
 * Model for cached Wiki Search
 *
 * @author Hok-Ming J. Zhong
 */
public class CachedWikiSearch {
    private Integer id;
    private String searchSubject;
    private String pageTitle;
    private String pageText;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSearchSubject() {
        return searchSubject;
    }

    public void setSearchSubject(String searchSubject) {
        this.searchSubject = searchSubject;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageText() {
        return pageText;
    }

    public void setPageText(String pageText) {
        this.pageText = pageText;
    }
}
