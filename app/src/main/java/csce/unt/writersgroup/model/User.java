package csce.unt.writersgroup.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Satya on 3/28/2017.
 */

public class User implements Serializable
{
    public static final String IS_ANCHOR = "true";
    public static final String NOT_IS_ANCHOR = "false";
    private String anchor;
    private String email;
    private String name;
    private int pages;
    private String password;
    private String uid;
    private long userType;


    public User()
    {
        //Default constructor to use with firebase
    }


    public User(String driverId, String name, String email)
    {
        this.uid = driverId;
        this.name = name;
        this.email = email;
    }
    public User(String driverId, String email)
    {
        this.uid = driverId;
        this.email = email;
    }

    public User(String id)
    {
        this.uid = id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(uid, name, email);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid) &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email);
    }

    @Override
    public String toString()
    {
        return "Writer{" +
                "name='" + name + '\'' +
                ", pages=" + pages +
                '}';
    }

    public String getAnchor()
    {
        return anchor;
    }

    public void setAnchor(String anchor)
    {
        this.anchor = anchor;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPages()
    {
        return pages;
    }

    public void setPages(int pages)
    {
        this.pages = pages;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;

    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public long getUserType()
    {
        return userType;
    }

    public void setUserType(long userType)
    {
        this.userType = userType;
    }

    @Exclude
    public boolean isAnAnchor()
    {
        return getAnchor().toLowerCase().equals("true");
    }
}
