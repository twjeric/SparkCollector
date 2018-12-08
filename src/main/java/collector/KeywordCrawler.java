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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class KeywordCrawler extends AbstractCollector {
    List<String> links = new LinkedList<>();  // next seeds
    String results;
    int interval;

    public KeywordCrawler() {
        this.interval = 3;
        links.add("https://www.ucla.edu");
    }

    public KeywordCrawler(String initalURL, int second) {
        this.interval = second;
        links.add(initalURL);
    }

    public KeywordCrawler(List<String> initalURLs, int second) {
        this.interval = second;
        links.addAll(initalURLs);
    }

    public String results() {
        return results;
    }

    public List<String> getAndProcess() {
        String USER_AGENT =
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
        List<String> words = new LinkedList<>();

        try {
            String seed = links.remove(0);
            Connection connection = Jsoup.connect(seed).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            Elements linksOnPage = htmlDocument.select("a[href]");
            Elements contents = htmlDocument.select("p");
            for (Element link : linksOnPage) {
                links.add(link.absUrl("href"));  // next
            }
            for (Element content : contents) {
                String[] wordsInContent = content.text().split(" ");  // result
                words.addAll(Arrays.stream(wordsInContent)
                        .filter(word -> word.length() > 3)
                        .collect(Collectors.toList()));
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        return words;
    }

    public void postProcess(JavaReceiverInputDStream<String> lines) {
        int rank = 20;
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
        KeywordCrawler crawler = new KeywordCrawler();
        List<String> lists = crawler.getAndProcess();
        System.out.println(lists);
    }
}
