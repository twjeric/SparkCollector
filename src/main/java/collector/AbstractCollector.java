package collector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.Serializable;
import java.util.List;

abstract public class AbstractCollector implements Serializable {
    int interval = 1;

    // get and process the results.
    List<String> getAndProcess() {
        return null;
    }

    // post process.
    void postProcess(JavaReceiverInputDStream<String> input) {
    }

    public String results() {
        return "";
    }

    public void run() throws Exception {
        Logger.getLogger("org").setLevel(Level.OFF);
        SparkConf sparkConf = new SparkConf()
                .setAppName("SparkCollector")
                .setMaster("local[4]")
                .set("spark.eventLog.enabled", "false");
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(interval));
        ssc.checkpoint("check");
        JavaReceiverInputDStream<String> lines = ssc.receiverStream(
                new CollectReceiver(this)
        );
        postProcess(lines);
        ssc.start();
        ssc.awaitTermination();
    }
}
