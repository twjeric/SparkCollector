package collector;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import java.util.LinkedList;
import java.util.List;

public class CollectReceiver extends Receiver<String> {
    private List<String> url = new LinkedList<>();
    private int iteration;
    private Crawler crawler = new Crawler();

    public CollectReceiver(String url , int iteration) {
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.url.add(url);
        this.iteration = iteration;
    }

    @Override
    public void onStart() {
        // Start the thread that receives data over a connection
        new Thread(this::receive).start();
    }

    @Override
    public void onStop() {
    }

    private void receive() {
        try {
            while (!isStopped() && this.iteration > 0) {
                List<String> links = crawler.crawl(this.url.get(0));
//                System.out.println("Found links: " + links.size());
                this.url.remove(0);
                this.url.addAll(links);
//                System.out.println(crawler.crawl(url.get(0)));
//                store(crawler.crawl(url.get(0)).get(0));
                url.forEach(this::store);
                this.iteration -= 1;
            }
//            restart("Trying to connect again");
        }  catch(Throwable t) {
            // restart if there is any other error
            restart("Error receiving data", t);
        }
    }
}