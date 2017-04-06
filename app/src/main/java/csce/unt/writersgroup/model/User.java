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
    private int pages;
    private String anchor;

    @Override
    public String toString()
    {
        return "Writer{" +
                "name='" + name + '\'' +
                ", pages=" + pages +
                '}';
    }

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
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + pages;
        return result;
    }

    public String getName()
    {
        return name;
    }

    public int getPages()
    {
        return pages;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPages(int pages)
    {
        this.pages = pages;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
