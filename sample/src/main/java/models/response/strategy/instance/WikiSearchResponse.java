package models.response.strategy.instance;

import data.ChatBotDataSource;
import exception.DataException;
import models.CachedWikiSearch;
import models.Intent;
import models.ResponseStrategy;
import models.TemplateText;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.chat.inference.Inference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Response Strategy to search Wikipedia for description.
 */
public class WikiSearchResponse extends ResponseStrategy {

    private final String SEARCH_API_KEY = "AIzaSyCIsbxr_KnL736zrCaioQ-lfx2h5jf04rU";
    private final String SEARCH_CX = "017290036556004623485:hy7vflumpke";
    private ChatBotDataSource ds;

    public void setDs(ChatBotDataSource ds) {
        this.ds = ds;
    }

    @Override
    public String createResponse(Intent intent, TemplateText templateText, Inference inference) {

        String searchSubject = inference.getTopic().getName();

        try {
            CachedWikiSearch cache = ds.getCachedWikiSearchBySubject(searchSubject.toUpperCase());
            if (cache == null) {
                String pageTitle = wikiSearch(searchSubject);
                if (pageTitle.contains("OMSCS:")) {
                    return omscsResponse(pageTitle);
                }
                return findWiki(searchSubject, pageTitle);
            } else if (cache.getPageText() == null || cache.getPageText().isEmpty()) {
                if (cache.getPageTitle().contains("OMSCS:")) {
                    return omscsResponse(cache.getPageTitle());
                }
                return findWiki(searchSubject, cache.getPageTitle());
            }

            return cache.getPageText();

        } catch (DataException e) {
            e.printStackTrace();
            return "An error occurred while looking for " + searchSubject;
        }

    }

    private String wikiSearch(String subject) {
        if (subject == null || subject.isEmpty()) {
            return null;
        }
        String title = null;

        try {
            String baseUrl = "https://www.googleapis.com/customsearch/v1?";
            String query = "key=" + SEARCH_API_KEY
                    + "&cx=" + SEARCH_CX
                    + "&q=" + URLEncoder.encode(subject, UTF_8.name());
            URL url = new URL(baseUrl + query);
            URLConnection conn = url.openConnection();

            try (InputStream response = conn.getInputStream();
                 BufferedReader in = new BufferedReader(new InputStreamReader(response))
            ) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject obj = new JSONObject(sb.toString());
                JSONArray items = obj.getJSONArray("items");
                String wikiBaseUrl = "en.wikipedia.org/wiki/";
                String omscsBaseUrl = "omscs.gatech.edu";

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String resultUrl = item.getString("link");
                    Integer wikiIndex = resultUrl.indexOf(wikiBaseUrl);
                    Integer omscsIndex = resultUrl.indexOf(omscsBaseUrl);
                    if (wikiIndex > 0) {
                        Integer startIndex = wikiIndex + wikiBaseUrl.length();
                        Integer endIndex = resultUrl.indexOf("&");
                        endIndex = endIndex > startIndex ?
                                endIndex - 1 : resultUrl.length();
                        title = resultUrl.substring(startIndex, endIndex);
                        System.out.println("Extracted " + title + " from " + resultUrl);

                        try {
                            cacheWikiSearch(subject, title, null);
                        } catch (DataException e) {
                            System.out.println("An error occurred trying to cache search result for subject: " + subject + " and title " + title);
                            e.printStackTrace();
                        }
                        return title;
                    }

                    if (omscsIndex > 0) {
                        title = item.getString("title");
                        try {
                            cacheWikiSearch(subject, title, null);
                        } catch (DataException e) {
                            e.printStackTrace();
                        }
                        return "OMSCS:" + title + "," + resultUrl;
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return title;
    }

    private void cacheWikiSearch(String subject, String title, String description) throws DataException {
        ds.cacheWikiSearch(subject.toUpperCase(), title, description);
    }

    private String findWiki(String searchSubject, String pageTitle) {
        String response = "Sorry. I couldn't find anything about ";

        if (pageTitle == null || pageTitle.isEmpty()) {
            return response + "that.";
        }

        response = response + pageTitle + ".";

        try {
            String baseUrl = "https://en.wikipedia.org/w/api.php?";
            String query = "action=query&prop=extracts&exintro=&explaintext=&format=json&titles=" + URLEncoder.encode(pageTitle, UTF_8.name());
            URL url = new URL(baseUrl + query);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "CS7637ChatBot/1.0 (hzhong41@gatech.edu)");

            try (InputStream urlResponse = conn.getInputStream();
                 BufferedReader in = new BufferedReader(new InputStreamReader(urlResponse))
            ) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject obj = new JSONObject(sb.toString());
                JSONObject pages = obj.getJSONObject("query").getJSONObject("pages");

                String description = "";
                if (pages.keys().hasNext()) {
                    String pageId = pages.keys().next();
                    description = pages.getJSONObject(pageId).getString("extract");
                }
                if (description != null && !description.isEmpty()) {
                    response = "Here's what I found on WikiPedia: <p class='wiki-quote'>" + description + "</p>";
                    cacheWikiSearch(searchSubject, pageTitle, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String omscsResponse(String pageInfo) throws DataException {
        String title = pageInfo.substring(6, pageInfo.indexOf(","));
        String url = pageInfo.substring(pageInfo.indexOf(",") + 1);
        String response = "You can find information at <a href='" + url + "'>" + title + "</a>";
        cacheWikiSearch(title, url, response);

        return response;
    }
}
