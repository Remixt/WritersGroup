package csce.unt.writersgroup.model;

import java.io.Serializable;

/**
 * Created by Satya on 3/28/2017.
 */

public class User implements Serializable
{
    private String uid;
    private String name;
    private String email;
    private String password;
    private int pages;
    private String anchor;
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
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + pages;
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (pages != user.pages) return false;
        return name != null ? name.equals(user.name) : user.name == null;

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

    public boolean isAnAnchor()
    {
        return getAnchor().toLowerCase().equals("true");
    }
}
