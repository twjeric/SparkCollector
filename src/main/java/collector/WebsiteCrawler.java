package collector;

import com.google.gson.Gson;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scala.Tuple2;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class WebsiteCrawler extends AbstractCollector {
    List<String> links = new LinkedList<>();  // next seeds
    String results;
    int interval;

    public WebsiteCrawler() {
        this.interval = 3;
        links.add("https://www.ucla.edu");
    }

    public WebsiteCrawler(String initalURL, int second) {
        this.interval = second;
        links.add(initalURL);
    }

    public WebsiteCrawler(List<String> initalURLs, int second) {
        this.interval = second;
        links.addAll(initalURLs);
    }

    public String results() {
        return results;
    }

    public List<String> getAndProcess() {
        String USER_AGENT =
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
        List<String> urls = new LinkedList<>();

        try {
            String seed = links.remove(0);
            Connection connection = Jsoup.connect(seed).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            Elements linksOnPage = htmlDocument.select("a[href]");
            for (Element link : linksOnPage) {
                links.add(link.absUrl("href"));  // next
                urls.add(link.baseUri().split("://")[1].split("/")[0]);  // result
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        return urls;
    }

    public void postProcess(JavaReceiverInputDStream<String> lines) {
        int rank = 10;
        JavaPairDStream<String, Long> count = lines.countByValueAndWindow(
                Durations.minutes(10), Durations.seconds(3)
        );
        JavaPairDStream<Long, String> order = count
                .mapToPair(Tuple2::swap)
                .transformToPair(s -> s.sortByKey(false));

        order.foreachRDD(rdd -> {
            List result = rdd.take(rank);
            results = new Gson().toJson(result);
            System.out.println(results);
        });
    }

    public static void main(String[] args) {
        WebsiteCrawler crawler = new WebsiteCrawler();
        List<String> lists = crawler.getAndProcess();
        System.out.println(lists);
    }
}
