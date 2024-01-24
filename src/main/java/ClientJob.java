import com.mchange.v2.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientJob implements Job {

    static Logger logger = Logger.getLogger(ClientJob.class.getName());

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            logger.info("ClientJob scheduler triggered");
            long startTime = System.nanoTime();
            clientJobProcess();
            long processingTime = (System.nanoTime() - startTime) / 1000000;
            logger.info("ClientJob completed in " + processingTime);
        } catch (Exception e) {
            logger.severe("Error occurred while executing ClientJob process");
            throw new RuntimeException(e);
        }
    }

    public void clientJobProcess(){
        try {
            logger.info("ClientJob started");
            String directoryPath = ApplicationProperties.getProperty(Utils.directoryPath);
            logger.info("Retrieved directory path:"+ directoryPath);
            String fileNameSuffix = ApplicationProperties.getProperty(Utils.fileNameSuffix);
            logger.info("Retrieved fileNameSuffix: "+ fileNameSuffix);
            List<String> fileNames = getFileList(directoryPath,fileNameSuffix);
            fileNames.forEach(fileName -> retrieveAndProcessFile(directoryPath + "/" + fileName));
            logger.info("ClientJob Ended");
        } catch (Exception e) {
            logger.severe("Error occurred while processing ClientJob");
            throw new RuntimeException(e);
        }
    }

    public void retrieveAndProcessFile(String fileName){
        try {
            logger.info("File processor started");
            HashMap<String,String> hashMap = saveRecordsfromFileToHashMap(fileName);
            filterKeysUsingRegex(hashMap,ApplicationProperties.getProperty(Utils.regexExpression));
            sendHashMaptoUrl(hashMap, ApplicationProperties.getProperty(Utils.serverUrl), ApplicationProperties.getProperty(Utils.charSet), ApplicationProperties.getProperty(Utils.contentType));
            deleteFile(fileName);
            logger.info("File successfully processed");
        } catch (Exception e) {
            logger.info("File processing failed for file: "+ fileName);
            throw new RuntimeException(e);
        }
    }

    public List<String> getFileList(String dirPath, String filePathSuffix){
        try {
            logger.info("Getting list of files");
            List<String> fileNames = Stream.of(new File(dirPath).listFiles())
                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .filter(name -> name.endsWith(filePathSuffix))
                    .collect(Collectors.toList());
            logger.info("Found " + fileNames.size() + " files in directory " + dirPath);
            return fileNames;
        } catch (Exception e) {
            logger.severe("Error occurred while retrieving file name at " + dirPath);
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, String> saveRecordsfromFileToHashMap(String fileName){
        try {
            logger.info("Getting properties from file " + fileName);
            HashMap<String,String> hashMap = Utils.getPropertiesFromFile(fileName);
            logger.info("Found " + hashMap.size() + " records in file");
            return  hashMap;
        } catch (Exception e) {
            logger.severe("Error occurred while retrieving record in file " + fileName);
            throw new RuntimeException(e);
        }
    }

    public static void filterKeysUsingRegex(HashMap<String, String> hashMap, String regex){
        try {
            logger.info("Filtering keys in hashmap with regex " + regex);
            Pattern pattern = Pattern.compile(regex);
            hashMap.entrySet().removeIf(mapEntry -> pattern.matcher(mapEntry.getKey()).matches());
            logger.info("Filtered hashmap size:" + hashMap.size());
        } catch (Exception e) {
            logger.severe("Error occurred while filtering hashMap");
            throw new RuntimeException(e);
        }
    }

    public static void sendHashMaptoUrl(HashMap<String, String> hashMap, String url, String charset, String contentType){
        try {
            logger.info("Sending hashMap to server");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            StringEntity request = new StringEntity(new JSONObject(hashMap).toString(), charset);
            request.setContentType(contentType);
            httpPost.setEntity(request);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            logger.info("Server response code:"+ response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            logger.severe("Error occurred while sending request to server at URL: " + url);
            throw new RuntimeException(e);
        }
    }

    private void deleteFile(String fileName) {
        if(new File(fileName).delete())
            logger.info("File successfully deleted");
        else
            logger.info("File deletion failed");

    }
}