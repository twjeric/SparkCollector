package collector;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import java.util.LinkedList;
import java.util.List;

public class CollectReceiver extends Receiver<String> {
    private List<String> links = new LinkedList<>();
    private int iteration;

    public CollectReceiver(String link, int iteration) {
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.links.add(link);
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
            while (!isStopped() && this.iteration > 0 && this.links.size() > 0) {
                String link = this.links.remove(0);
                Crawler crawler = new Crawler(link);
                List<String> links = crawler.getLinks();
                this.links.addAll(links);
//                System.out.println(crawler.crawl(links.get(0)));
//                store(crawler.crawl(links.get(0)).get(0));
                crawler.baseUrls().forEach(this::store);
                this.iteration -= 1;
            }
        }  catch(Throwable t) {
            // restart if there is any other error
            restart("Error receiving data", t);
        }
    }
}