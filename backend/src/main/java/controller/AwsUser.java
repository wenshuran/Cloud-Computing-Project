package controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "awsUser")
public class AwsUser {
    private String username = null;
    private String password = null;
    public AwsUser(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }
    //主键
    @DynamoDBHashKey(attributeName = "username")
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public AwsUser() {}

    //配有索引 userName-index
    @DynamoDBAttribute(attributeName = "password")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}