package collector;

import javafx.util.Duration;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Network {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {
//        if (args.length < 2) {
//            System.err.println("Usage: JavaNetworkWordCount <hostname> <iteration>");
//            System.exit(1);
//        }
        Logger.getLogger("org").setLevel(Level.OFF);

        SparkConf sparkConf = new SparkConf()
                .setAppName("SparkCollector")
                .setMaster("local[4]")
                .set("spark.eventLog.enabled", "false");
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(5));
        ssc.checkpoint("check");


        JavaReceiverInputDStream<String> lines = ssc.receiverStream(
                new CollectReceiver("http://www.bbc.com", 115)
        );
        JavaPairDStream<String, Long> count = lines.countByValueAndWindow(Durations.minutes(10), Durations.seconds(5));
        count.print();
        ssc.start();
        ssc.awaitTermination();
    }
}