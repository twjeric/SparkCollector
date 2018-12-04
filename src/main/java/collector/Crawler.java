package collector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private Document htmlDocument;
    private List<String> links = new LinkedList<>();
    private List<String> urls = new LinkedList<>();


    public Crawler(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            Elements linksOnPage = htmlDocument.select("a[href]");
            for (Element link : linksOnPage) {
                links.add(link.absUrl("href"));
                urls.add(link.baseUri().split("://")[1]);
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public List<String> baseUrls() {
        return urls;
    }

    public List<String> getLinks() {
        return links;
    }

    public boolean searchForWord(String searchWord) {
        if (this.htmlDocument == null) {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = this.htmlDocument.body().text();
        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }



    public static void main(String[] args) {
        Crawler crawler = new Crawler("https://jsoup.org");
        List<String> lists = crawler.getLinks();
        System.out.println(lists);
    }
}