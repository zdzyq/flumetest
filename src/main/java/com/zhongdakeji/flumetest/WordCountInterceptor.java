package com.zhongdakeji.flumetest;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * flumetest com.zhongdakeji
 * <p>
 * Create by zyq on 2018/12/21 0021
 */
public class WordCountInterceptor implements Interceptor {
    private String sword;

    public String getSword() {
        return sword;
    }
    public void setSword(String sword) {
        this.sword = sword;
    }
    //初始化拦截器，一般用于属性填值
    public void initialize() {

    }
    //拦截业务逻辑方法
    public Event intercept(Event event) {
        //如果sword不为空则统计sword单词在event中出现的次数
        //如果sword为空的话event中所有单词的数量
        String line = new String(event.getBody());
        String[] words = line.split("[\\s,]+");
        int count = 0;
        for(String word:words){
            if(sword!=null){
                if(word.equals(sword)){
                    count += 1;
                }
            }else{
                count += 1;
            }
        }
        Map<String,String> headers = event.getHeaders();
        if(sword!=null){
            headers.put(sword, String.valueOf(count));
        }else{
            headers.put("words", String.valueOf(count));
        }
        event.setHeaders(headers);
        return event;
    }
    //批量拦截业务逻辑方法
    public List<Event> intercept(List<Event> events) {
        List<Event> retrunList = new ArrayList<Event>();
        for(Event event:events){
            Event newEvent = intercept(event);
            retrunList.add(newEvent);
        }
        return retrunList;
    }
    //interceptor执行结束，资源释放（如果需要的话）
    public void close() {

    }

    public static class Builder implements Interceptor.Builder{
        private String sword;
        public WordCountInterceptor build(){
            WordCountInterceptor interceptor = new WordCountInterceptor();
            interceptor.setSword(sword);
            return interceptor;
        }
        //从用户的conf代码中获取用户的配置参数
        public void configure(Context context) {
            sword = context.getString("sword");
        }
    }
}
