import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import java.nio.charset.StandardCharsets;

/** Creates /demo/config on first run and updates its data on later runs. */
public class ConfigPublisher {
    public static void main(String[] args) throws Exception {
        // usage: java ConfigPublisher <hosts> <value>
        ZooKeeper zk = ZkConnect.connect(args[0], 15000);
        byte[] value = args[1].getBytes(StandardCharsets.UTF_8);
        ensure(zk, "/demo");
        if (zk.exists("/demo/config", false) == null) {
            zk.create("/demo/config", value, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                      CreateMode.PERSISTENT);
            System.out.println("created /demo/config = " + args[1]);
        } else {
            zk.setData("/demo/config", value, -1);   // -1 = any version
            System.out.println("updated /demo/config = " + args[1]);
        }
        zk.close();
    }

    static void ensure(ZooKeeper zk, String path) throws Exception {
        try {
            zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                      CreateMode.PERSISTENT);
        } catch (KeeperException.NodeExistsException e) {
            // already created by another client: nothing to do
        }
    }
}
