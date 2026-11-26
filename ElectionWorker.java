import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/** A worker that joins a group and takes part in leader election. */
public class ElectionWorker implements Watcher {
    private static final String ELECTION = "/demo/election";
    private final ZooKeeper zk;
    private final String name;
    private String myNode;                       // full path of our znode

    ElectionWorker(ZooKeeper zk, String name) {
        this.zk = zk;
        this.name = name;
    }

    void volunteer() throws Exception {
        ConfigPublisher.ensure(zk, "/demo");
        ConfigPublisher.ensure(zk, ELECTION);
        byte[] data = name.getBytes(StandardCharsets.UTF_8);
        myNode = zk.create(ELECTION + "/n_", data,
                           ZooDefs.Ids.OPEN_ACL_UNSAFE,
                           CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(name + ": created " + myNode);
        check();
    }

    void check() throws Exception {
        List<String> children = zk.getChildren(ELECTION, false);
        Collections.sort(children);   // zero-padded: text order works
        String mine = myNode.substring(myNode.lastIndexOf('/') + 1);
        int index = children.indexOf(mine);
        if (index == 0) {
            System.out.println(name + ": I am the LEADER");
            return;
        }
        String predecessor = children.get(index - 1);
        if (zk.exists(ELECTION + "/" + predecessor, this) == null) {
            check();            // predecessor vanished meanwhile
        } else {
            System.out.println(name + ": follower, watching "
                               + predecessor);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted) {
            try {
                check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // usage: java ElectionWorker <name> <hosts>
        ZooKeeper zk = ZkConnect.connect(args[1], 15000);
        new ElectionWorker(zk, args[0]).volunteer();
        Thread.sleep(Long.MAX_VALUE);
    }
}
