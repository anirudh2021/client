import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.SchedulerFactory;

import java.util.logging.Logger;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main {

    static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        logger.info("Start");
        init(args);
        if(new ApplicationProperties().getProperty(Utils.enableSchedulerFlag).equals("true"))
            startClientJobScheduler();
        else
            startClientJob();
        logger.info("End");
    }
    public static void init(String[] args){
        try {
            logger.info("Retrieving properties from "+args[0]);
            new ApplicationProperties().importPropertiesFromFile(args[0]);
            logger.info("Application properties successfully retrieved");
        } catch (Exception e) {
            logger.info("Error occurred while reading Application properties");
            throw new RuntimeException(e);
        }
    }

    public static void startClientJob(){
        try {
            logger.info("calling clientJobProcess");
            new ClientJob().clientJobProcess();
            logger.info("clientJob Process completed");
        } catch (Exception e) {
            logger.info("Error occurred while executing ClientJob process");
            throw new RuntimeException(e);
        }
    }

    public static void startClientJobScheduler(){
        try {
            logger.info("Setting clientJob scheduler");
            String cronExpression = ApplicationProperties.getProperty(Utils.schedulerCronExpression);
            logger.info("Cron expression for client job: "+ cronExpression);

            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            JobDetail job = newJob(ClientJob.class)
                    .withIdentity("ClientJob", "group1")
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity("cronExpression", "group1")
                    .startNow()
                    .withSchedule(cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(job, trigger);
            scheduler.start();
            logger.info("Started ClientJob scheduler");
        } catch (SchedulerException e) {
            logger.severe("Error while creating ClientJob scheduler");
            throw new RuntimeException(e);
        }
    }
}
