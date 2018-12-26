package com.zhongdakeji.sqooptest;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.*;
import org.apache.sqoop.validation.Status;


/**
 * flumetest sqooptest
 * <p>
 * Create by zyq on 2018/12/24 0024
 */
public class FactRentalImport {
    private boolean isIncremental = true;
    private String jobName = "fact_rental";
    private SqoopClient client = CreateLinkTest.getSqoopClient();
    //每一次导入都需要重新创建job,params代表具体某一天的job,params是日期参数
    //job的具体名称fact_rental_20180703,fact_rental_20180704
    public boolean createJob(String params){
        String fullJobName = jobName+"_"+params;
        MJob job = null;
        try{
            job = client.getJob(fullJobName);
            client.deleteJob(fullJobName);
        }catch(Exception e){
            System.out.println("当前job不存在："+fullJobName);
        }
        job = client.createJob("mysql_sakila", "hdfsdir");
        job.setName(fullJobName);
        MFromConfig fromConfig = job.getFromJobConfig();
        MToConfig toConfig = job.getToJobConfig();
        MDriverConfig driverConfig = job.getDriverConfig();
        CreateLinkTest.printConfigParams(fromConfig.getConfigs());
        CreateLinkTest.printConfigParams(toConfig.getConfigs());
        CreateLinkTest.printConfigParams(driverConfig.getConfigs());
        String sql = " SELECT a.rental_id	as rental_id							     "+
                "        ,a.last_update								     "+
                "        ,a.customer_id								     "+
                "        ,a.staff_id								     "+
                "        ,b.film_id								     "+
                "        ,b.store_id								     "+
                "        ,a.rental_date								     "+
                "        ,a.return_date								     "+
                " FROM rental a									     "+
                " INNER JOIN inventory b							     "+
                " ON a.inventory_id=b.inventory_id						     "+
                " where DATE_FORMAT(a.last_update,'%Y%m%d')='"+ params +"' and ${CONDITIONS}";
//		fromConfig.getStringInput("fromJobConfig.schemaName").setValue("sakila");
        fromConfig.getStringInput("fromJobConfig.sql").setValue(sql);
        fromConfig.getStringInput("fromJobConfig.partitionColumn").setValue("rental_id");
        toConfig.getEnumInput("toJobConfig.outputFormat").setValue("TEXT_FILE");
        toConfig.getStringInput("toJobConfig.outputDirectory").setValue("/salila/ods/fact_rental/"+params);
        toConfig.getBooleanInput("toJobConfig.appendMode").setValue(false);
        driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(4);
        driverConfig.getIntegerInput("throttlingConfig.numLoaders").setValue(4);
        Status status = client.saveJob(job);
        if(status.canProceed()){
            System.out.println("job创建成功："+job.getName());
            return true;
        }else{
            System.out.println("job创建失败："+job.getName());
            return false;
        }
    }
    public void startJob(String params){
        String fullJobName = jobName+"_"+params;
        MSubmission submission = client.startJob(fullJobName);
        if(submission.getStatus().isRunning() && submission.getProgress() != -1) {
            System.out.println("Progress : " + String.format("%.2f %%", submission.getProgress() * 100));
        }
    }
    public void batchOperate(String params){
        boolean isCreated = createJob(params);
        if(isCreated){
            startJob(params);
        }
    }
    public static void main(String[] args){
        String dateDay = args[0];
        FactRentalImport fr = new FactRentalImport();
        fr.batchOperate(dateDay);
    }
}
