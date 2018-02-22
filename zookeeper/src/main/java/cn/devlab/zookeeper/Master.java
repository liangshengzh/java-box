package cn.devlab.zookeeper;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

public class Master implements Watcher{
    ZooKeeper zk;
    String hostPort;
    private Random random = new Random(this.hashCode());
    String serverId = Integer.toHexString(random.nextInt());
    boolean isLeader = false;
    public Master(String hostPort) {
        this.hostPort = hostPort;
    }

    void statZK(){
        try {
            zk = new ZooKeeper(hostPort, 15000, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    void runForMaster(){
      while (true){
          try {
              zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
              isLeader = true;
              break;
          } catch (KeeperException.NodeExistsException e) {
              e.printStackTrace();
              isLeader = false;
              break;
          }catch (KeeperException e){
              e.printStackTrace();
          }catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
    }

    boolean checkMaster(){
        while (true){
            Stat stat = new Stat();
            try {
                byte[] data = zk.getData("/master",false,stat);
                isLeader = new String(data).equals(serverId);
                return true;
            } catch (KeeperException.NoNodeException e) {
                e.printStackTrace();
                return false;
            }catch (KeeperException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Master master = new Master("localhost:2181");
        master.statZK();
        master.runForMaster();
        if(master.isLeader){
            System.out.println("I am the leader");
            Thread.sleep(60000);
        }else{
            System.out.println("someone else is the leader");
        }

    }
}
