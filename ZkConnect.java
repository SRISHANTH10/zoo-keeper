import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.util.concurrent.CountDownLatch;

/** Opens a ZooKeeper session and waits until it is connected. */
public class ZkConnect {
    public static ZooKeeper connect(String hosts, int timeoutMs)
            throws Exception {
        CountDownLatch connected = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper(hosts, timeoutMs, event -> {
            if (event.getType() == Watcher.Event.EventType.None) {
                System.out.println("[session] " + event.getState());
                if (event.getState()
                        == Watcher.Event.KeeperState.SyncConnected) {
                    connected.countDown();
                }
            }
        });
        connected.await();
        return zk;
    }
}
