package com.zhongdakeji.flumetest;

import org.apache.flume.Event;
import org.apache.flume.api.RpcClient;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * flumetest com.zhongdakeji
 * <p>
 * Create by zyq on 2018/12/21 0021
 */
public class SelectorTest {
    //随机产生数据，加上地区标识,地区有henan、shandong、shanghai、beijing
    //要求河南的数据存放到hdfs的/flume/s_henan目录
    //要求山东的的数据存放到hdfs的/flume/s_shandong目录
    // 要求其他的的数据存放到hdfs的/flume/s_qita目录
    public static void main(String[] args) throws Exception{
        Random random = new Random();
        String[] provinces = new String[]{"henan","shandong","shanghai","beijing"};
        RpcClient client = FlumeClientUitls.getClient();
        for(int i=0;i<1000;i++){
            String province = provinces[random.nextInt(provinces.length)];
            Event event = EventBuilder.withBody("msg,"+province+","+i, Charset.forName("UTF-8"));
            Map<String,String> header = new HashMap<String,String>();
            header.put("province", province);
            event.setHeaders(header);
            System.out.println("msg,"+province+","+i);
            client.append(event);
        }
        client.close();
    }
}
