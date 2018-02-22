package cn.devlab.zookeeper;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Random;

public class Worker implements Watcher{

    ZooKeeper zk;
    String hostPort;
    Random random = new Random(this.hashCode());
    String serverId = Integer.toHexString(random.nextInt());

    public Worker(String hostPort) {
        this.hostPort = hostPort;
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    void statZK(){
        try {
            zk = new ZooKeeper(hostPort, 15000, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void bootstrap(){
        createParent("/workers",new byte[0]);
        createParent("/assign",new byte[0]);
        createParent("/tasks",new byte[0]);
        createParent("/status",new byte[0]);
    }

    void createParent(String path, byte[] data){
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,callback,data );
    }


    AsyncCallback.StringCallback callback = new AsyncCallback.StringCallback() {
        public void processResult(int i, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(i)){
                case CONNECTIONLOSS:
                    createParent(path, (byte[])ctx);
                    break;
                case OK:
                    System.out.println("parent created");
                    break;
                case NODEEXISTS:
                    System.out.println("parent already exist");
                    break;
                default:
                    System.out.println("something went wrong");
            }
        }
    };

    void register(){
        zk.create("/workers/worker-"+ serverId, "Idle".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL,callback, null);
    }

    public static void main(String[] args) throws Exception{
        Worker worker = new Worker("localhost:2181");
        worker.statZK();
        worker.register();
        Thread.sleep(30000);
    }
}
