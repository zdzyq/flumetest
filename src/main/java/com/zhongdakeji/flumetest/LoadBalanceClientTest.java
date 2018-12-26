package com.zhongdakeji.flumetest;

import org.apache.flume.Event;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * flumetest com.zhongdakeji
 * <p>
 * Create by zyq on 2018/12/21 0021
 */
public class LoadBalanceClientTest {
    public static void main(String[] args) throws Exception, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/loadbalance_client.properties"));
        RpcClient client = RpcClientFactory.getInstance(properties);
        for(int i=0;i<200;i++){
            Event event = EventBuilder.withBody("msg"+i, Charset.forName("UTF-8"));
            client.append(event);
        }
        client.close();
    }
}
