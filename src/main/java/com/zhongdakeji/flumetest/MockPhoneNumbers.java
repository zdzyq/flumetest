package com.zhongdakeji.flumetest;

import org.apache.flume.Event;
import org.apache.flume.api.RpcClient;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;
import java.util.Random;

/**
 * flumetest com.zhongdakeji
 * <p>
 * Create by zyq on 2018/12/21 0021
 */
public class MockPhoneNumbers {
    public static void main(String[] args) throws Exception{
        String[] no_frags = new String[]{"135","136","138","158","139","152","130","131"};
        Random random = new Random();
        RpcClient client = FlumeClientUitls.getClient();
        for(int i=0;i<100;i++){
            String pre = no_frags[random.nextInt(no_frags.length)];
            for(int j=0;j<8;j++){
                pre += random.nextInt(10);
            }
            System.out.println(pre);
            Event event = EventBuilder.withBody("pn:"+pre+"id:"+i+",name:user_"+i, Charset.forName("utf-8"));
            client.append(event);
        }
        client.close();
    }
}
