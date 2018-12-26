package com.zhongdakeji.flumetest;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;

/**
 * flumetest com.zhongdakeji
 * <p>
 * Create by zyq on 2018/12/21 0021
 */
public class FlumeClientUitls {
    private static final String HOST_NAME = "bigdata01";
    private static final int PORT = 8888;

    public static RpcClient getClient(){
        RpcClient client = RpcClientFactory.getDefaultInstance(HOST_NAME, PORT);
        return client;
    }
    public static void clientClose(RpcClient client){
        client.close();
    }
    public static void sendData(RpcClient client, String msg){
        Event event = EventBuilder.withBody(msg, Charset.forName("UTF-8"));
        try {
            client.append(event);
        } catch (EventDeliveryException e) {
            e.printStackTrace();
            System.out.println("发送消息["+msg+"]失败");
        }
    }
    public static void main(String[] args){
        RpcClient client = FlumeClientUitls.getClient();
        for(int i=0;i<20;i++){
            sendData(client, "msg"+i);
        }
        clientClose(client);
    }
}
