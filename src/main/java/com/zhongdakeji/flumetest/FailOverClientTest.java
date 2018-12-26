package com.zhongdakeji.flumetest;

import org.apache.flume.Event;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * flumetest com.zhongdakeji
 * <p>
 * Create by zyq on 2018/12/21 0021
 */
public class FailOverClientTest {
    //	服务端：bigdata01 bigdata02
//	端口号：8888
//	激活节点是bigdata01 ，备用节点是bigdata02,当bigdata01挂了，bigdata02接替bigdata01继续采集数据
    public static void main(String[] args) throws Exception{
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream("src/main/resources/failover_client.properties");
        properties.load(inputStream);
        RpcClient client = RpcClientFactory.getInstance(properties);
        for(int i=0;i<100;i++){
            Event event = EventBuilder.withBody("msg"+i, Charset.forName("UTF-8"));
            client.append(event);
            Thread.sleep(2000);
        }
        client.close();
    }
}
