package cn.devlab.zookeeper.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Main {

    public static void main(String[] args)throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181",3500000,100000,retryPolicy);
        client.start();

        InterProcessMutex mutex = new InterProcessMutex(client, "/curator/lock");
        mutex.acquire();
       // Thread.sleep(20000);
        System.out.println("enter mutex");
        mutex.release();
        client.close();
    }
}
