import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import java.nio.charset.StandardCharsets;

/** Reads /demo/config and prints a line each time its data change. */
public class ConfigWatcher implements Watcher {
    private final ZooKeeper zk;

    ConfigWatcher(ZooKeeper zk) { this.zk = zk; }

    void read() throws Exception {
        Stat stat = new Stat();
        // passing "this" registers a new one-time watch on every read
        byte[] data = zk.getData("/demo/config", this, stat);
        String text = new String(data, StandardCharsets.UTF_8);
        System.out.println("config = " + text
                           + " (version " + stat.getVersion() + ")");
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDataChanged) {
            try {
                read();      // read again: this sets a new watch
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println("config znode was deleted");
        }
    }

    public static void main(String[] args) throws Exception {
        // usage: java ConfigWatcher <hosts>   (run ConfigPublisher first)
        ZooKeeper zk = ZkConnect.connect(args[0], 15000);
        new ConfigWatcher(zk).read();
        Thread.sleep(Long.MAX_VALUE);
    }
}
