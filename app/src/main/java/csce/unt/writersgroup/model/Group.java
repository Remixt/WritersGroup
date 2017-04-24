package csce.unt.writersgroup.model;

import java.io.Serializable;

/**
 * Created by Satyanarayana on 4/5/2017.
 */
public class Group implements Serializable {

    private String groupId;
    private String users;
    private String anchors;

    public Group(String groupName)
    {
        this.groupId = groupName;
        users = "";
        anchors = "";
    }

    public Group()
    {

    }

    @Override
    public int hashCode()
    {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (anchors != null ? anchors.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (groupId != null ? !groupId.equals(group.groupId) : group.groupId != null) return false;
        if (users != null ? !users.equals(group.users) : group.users != null) return false;
        return anchors != null ? anchors.equals(group.anchors) : group.anchors == null;

    }

    @Override
    public String toString()
    {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", users='" + users + '\'' +
                ", anchors='" + anchors + '\'' +
                '}';
    }

    public String getAnchors()
    {
        return anchors;
    }

    public void setAnchors(String anchors)
    {
        this.anchors = anchors;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getUsers()
    {
        return users;
    }

    public void setUsers(String users)
    {
        this.users = users;
    }
}
