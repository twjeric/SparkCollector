package collector;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import java.util.List;

public class CollectReceiver extends Receiver<String> {
    private int iteration = 100;
    private AbstractCollector collect;

    public CollectReceiver(AbstractCollector collect) {
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.collect = collect;
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
                List<String> urls = this.collect.getAndProcess();
                urls.forEach(this::store);
                this.iteration -= 1;
            }
        }  catch(Throwable t) {
            // restart if there is any other error
            restart("Error receiving data", t);
        }
    }
}