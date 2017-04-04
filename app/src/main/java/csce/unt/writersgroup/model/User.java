package csce.unt.writersgroup.model;

import java.io.Serializable;

/**
 * Created by Satya on 3/28/2017.
 */

public class User implements Serializable
{
    private String userId;
    private String name;
    private String email;
    private String password;

    public User()
    {
        //Default constructor to use with firebase
    }

    public User(String driverId, String name, String email)
    {
        this.userId = driverId;
        this.name = name;
        this.email = email;
    }

    public User(String driverId, String email)
    {
        this.userId = driverId;
        this.email = email;
    }

    public User(String id)
    {
        this.userId = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
