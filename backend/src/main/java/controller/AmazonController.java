package controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/")
public class AmazonController {
    private String accessKeyID = "AKIAWAV7CBP3SGZV36HH";
    private String secretKey = "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";
    private String bucket = "ccwenshuran";
    //表名
    private static String TABLE_NAME = "awsUser";
    //用户凭证对象
    private static AWSCredentialsProvider awsCredentialsProvider = new AWSCredentialsProvider() {
        private String accessKeyID = "AKIAWAV7CBP3QXXTHR7W";
        private String secretKey = "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";
        public void refresh() {}
        public AWSCredentials getCredentials() {
            return new BasicAWSCredentials(accessKeyID, secretKey);
        }
    };
    //表的相关对象
    private static AmazonDynamoDB amazonDynamoDBClient = null;
    private static DynamoDBMapper dbMapper = null;
    private static Table table = null;
    private AmazonS3 s3client;

    public AmazonController(){
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyID, secretKey);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignerOverride("S3SignerType");//凭证验证方式
        clientConfig.setProtocol(Protocol.HTTP);//访问协议
        s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();

        amazonDynamoDBClient =  AmazonDynamoDBClientBuilder.standard()
                                    .withCredentials(awsCredentialsProvider)
                                    .withRegion(Regions.EU_WEST_1)
                                    .build();
        dbMapper = new DynamoDBMapper(amazonDynamoDBClient);
        table = new DynamoDB(amazonDynamoDBClient).getTable(TABLE_NAME);
    }

    private static AwsUser getItemByUsername(String username) {
        return dbMapper.load(AwsUser.class, username);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    private ResponseEntity<Object> login(@RequestBody User user) {
        AwsUser awsUser = getItemByUsername(user.getUsername());
        if (awsUser == null){
            return new ResponseEntity<Object>(null, HttpStatus.UNAUTHORIZED);
        }
        if (awsUser.getPassword().equals(user.getPassword())){
            return new ResponseEntity<Object>(null, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<Object>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @CrossOrigin("*")
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public static void addOneItem(@RequestBody AwsUser user) {
        dbMapper.save(user);
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    private ResponseEntity<Object> findUser(@RequestBody Username username) {
        AwsUser awsUser = getItemByUsername(username.getUsername());
        if (awsUser != null){
            return new ResponseEntity<Object>(null, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<Object>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/amazonS3/addEvent", method = RequestMethod.POST)
    private boolean addEvent(@RequestBody Event event){
        try {
            System.out.println(event.getUser() + "/" + event.getTitle()+"@"+event.getEndTime().substring(0, 10));
            File temp = new File(event.getTitle()+"@"+event.getEndTime().substring(0, 10));
            temp.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(event.getTitle());
            out.newLine();
            out.write(event.getOrganiser());
            out.newLine();
            out.write(event.getStartTime());
            out.newLine();
            out.write(event.getEndTime());
            out.newLine();
            out.write(event.getContent());
            out.close();
            FileInputStream fileInputStream = new FileInputStream(temp);
            MultipartFile file = new MockMultipartFile(temp.getName(), temp.getName(),
                            ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
            ObjectMetadata metadata = new ObjectMetadata();
            //System.out.println(file.isEmpty());
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.addUserMetadata("x-amz-meta-title", "aws file");
            PutObjectResult result = s3client.putObject(new PutObjectRequest(bucket, event.getUser() + "/" + file.getOriginalFilename(), file.getInputStream(), metadata));
            temp.delete();
            return true;
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            return false;
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "/amazonS3/editEvent", method = RequestMethod.POST)
    private boolean editEvent(@RequestBody Event event){
        try {
            String user = event.getUser().replaceAll("\r|\n", "");;
            String title = event.getTitle().replaceAll("\r|\n", "");
            String time = event.getEndTime().replaceAll("\r|\n", "").substring(0,10);
            String name = title + "@" + time;
            //System.out.println(name);
            File temp = new File(name);
            temp.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(event.getTitle());
            out.newLine();
            out.write(event.getOrganiser());
            out.newLine();
            out.write(event.getStartTime());
            out.newLine();
            out.write(event.getEndTime());
            out.newLine();
            out.write(event.getContent());
            out.close();
            FileInputStream fileInputStream = new FileInputStream(temp);
            MultipartFile file = new MockMultipartFile(temp.getName(), temp.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
            ObjectMetadata metadata = new ObjectMetadata();
            //System.out.println(file.isEmpty());
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.addUserMetadata("x-amz-meta-title", "aws file");
            PutObjectResult result = s3client.putObject(new PutObjectRequest(bucket, file.getOriginalFilename(), file.getInputStream(), metadata));
            temp.delete();
            return true;
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            return false;
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "/amazonS3/list", method = RequestMethod.POST)
    private ResponseEntity<Object> listObjects(@RequestBody Username username){
        ArrayList<VueTable> arrayList= new ArrayList<>();
        ObjectListing objectListing = s3client.listObjects(bucket, username.getUsername());
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            String tmp = os.getKey();
            if (!tmp.contains(username.getUsername() + "/")){
                continue;
            }
            if (tmp.equals(username.getUsername()+"/")){
                continue;
            }
            tmp = tmp.replace(username.getUsername()+"/", "");
            //System.out.println(tmp);
            int index = tmp.indexOf("@");
            arrayList.add(new VueTable(username.getUsername(), tmp.substring(0, index), tmp.substring(index+1)));
    }
        return new ResponseEntity<Object>(arrayList, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    private ResponseEntity<Object> deleteObjects(@RequestBody VueTable table){
        s3client.deleteObject(bucket,table.getUser() + "/" + table.getTitle()+"@"+table.getDate());
        return new ResponseEntity<Object>(null, HttpStatus.OK);
    }

    @CrossOrigin("*")
    @RequestMapping(value = "/share", method = RequestMethod.POST)
    private ResponseEntity<Object> share(@RequestBody ShareEvent shareEvent){
        for (VueTable vueTable : shareEvent.getTables()){
            s3client.copyObject(bucket, shareEvent.getFrom()+"/" + vueTable.getTitle() + "@" +
                    vueTable.getDate(), bucket, shareEvent.getTo()+"/" + vueTable.getTitle() + "@" + vueTable.getDate());
        }

        return new ResponseEntity<Object>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/getObject", method = RequestMethod.POST)
    private ResponseEntity<Object> download(@RequestBody VueTable object) throws IOException {
        S3Object s3object = s3client.getObject(bucket, object.getUser() + "/" + object.getTitle()+"@"+object.getDate());
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        int i;
        String title = "";
        while((i = inputStream.read()) != -1){
            char c=(char)i;
            if (c == '\n')
                break;
            title = title + c;
        }
        String organiser = "";
        while((i = inputStream.read()) != -1){
            char c=(char)i;
            if (c == '\n')
                break;
            organiser = organiser + c;
        }
        String startTime = "";
        while((i = inputStream.read()) != -1){
            char c=(char)i;
            if (c == '\n')
                break;
            startTime = startTime + c;
        }
        startTime = startTime.replace("T", " ");
        startTime = startTime.replace("Z", "");
        String endTime = "";
        while((i = inputStream.read()) != -1){
            char c=(char)i;
            if (c == '\n')
                break;
            endTime = endTime + c;
        }
        endTime = endTime.replace("T", " ");
        endTime = endTime.replace("Z", "");
        String content = "";
        while((i = inputStream.read()) != -1){
            char c=(char)i;
            content = content + c;
        }
        Event event = new Event(object.getUser(), title, organiser, startTime, endTime, content);
        return new ResponseEntity<Object>(event, HttpStatus.OK);
    }
}