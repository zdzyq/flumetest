package com.zhongdakeji.sqooptest;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.*;
import org.apache.sqoop.validation.Status;

import java.util.List;

/**
 * flumetest sqooptest
 * <p>
 * Create by zyq on 2018/12/24 0024
 */
public class CreateLinkTest {
    private static final String URL = "http://bigdata02:12000/sqoop/";
    private static SqoopClient client = new SqoopClient(URL);

    public static SqoopClient getSqoopClient(){
        return new SqoopClient(URL);
    }
    public static void createJdbcLinkTest(){
        MLink link = client.createLink("generic-jdbc-connector");
        link.setName("mysql_sakila");
        //获取link的配置参数对象
        MLinkConfig linkConfig = link.getConnectorLinkConfig();
        //在linkConfig上对link的各种该参数进行设置
        List<MConfig> configList = linkConfig.getConfigs();
        printConfigParams(configList);
        //根据打印内容来写配置代码
        linkConfig.getStringInput("linkConfig.jdbcDriver").setValue("com.mysql.jdbc.Driver");
        linkConfig.getStringInput("linkConfig.connectionString").setValue("jdbc:mysql://localhost:3306/sakila");
        linkConfig.getStringInput("linkConfig.username").setValue("root");
        linkConfig.getStringInput("linkConfig.password").setValue("123456");
        linkConfig.getStringInput("dialect.identifierEnclose").setValue("`");
        Status status = client.saveLink(link);
        if(status.canProceed()){
            System.out.println("link 创建成功" + link.getName());
        }else{
            System.out.println("link 创建失败" + link.getName());
        }
    }
    public static void printConfigParams(List<MConfig> configList){
        for(MConfig config:configList){
            List<MInput<?>> inputList = config.getInputs();
            for(MInput input:inputList){
                System.out.println(input);
            }
        }
    }
    public static void testJob(){
        MJob job = client.createJob("java_jdbc_link", "hdfsdir");
        job.setName("java_jdbc_to_hdfs");
        MFromConfig fromConfig = job.getFromJobConfig();
        MToConfig toConfig = job.getToJobConfig();
        MDriverConfig driverConfig = job.getDriverConfig();
        printConfigParams(job.getFromJobConfig().getConfigs());
        printConfigParams(job.getToJobConfig().getConfigs());
        printConfigParams(job.getDriverConfig().getConfigs());
        String sql = "select * from course where ${CONDITIONS}";
        fromConfig.getStringInput("fromJobConfig.sql").setValue(sql);
        fromConfig.getStringInput("fromJobConfig.partitionColumn").setValue("C");
//		fromConfig.getStringInput("fromJobConfig.schemaName").setValue("test3");
        toConfig.getEnumInput("toJobConfig.outputFormat").setValue("TEXT_FILE");
        toConfig.getStringInput("toJobConfig.outputDirectory").setValue("/sqoop/course");
        toConfig.getBooleanInput("toJobConfig.appendMode").setValue(true);
        driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(1);
        driverConfig.getIntegerInput("throttlingConfig.numLoaders").setValue(1);
        Status status = client.saveJob(job);
        if(status.canProceed()){
            System.out.println("创建job成功"+ job.getName());
        }else{
            System.out.println("创建job失败"+ job.getName());
        }
    }
    public static void startJob(){
        MSubmission submission = client.startJob("java_jdbc_to_hdfs");
        if(submission.getStatus().isRunning() && submission.getProgress() != -1) {
            System.out.println("Progress : " + String.format("%.2f %%", submission.getProgress() * 100));
        }
    }
    public static void main(String[] args){
//		testJob();
//		startJob();
        createJdbcLinkTest();
    }

}
