package csce.unt.writersgroup.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Satya on 3/28/2017.
 */

public class Session implements Serializable
{
    public static final String SESSION_STARTED = "true";
    public static final String SESSION_NOT_STARTED = "false";
    public static DateFormat sessionIDFormatter = new SimpleDateFormat("MMddyyyy", Locale
            .getDefault());
    private String sessionId;
    private String groups = "";
    private String started = SESSION_NOT_STARTED;
    private Map<String, Object> users;
    public Session()
    {

    }

    public static String generateNewSessionID()
    {
        return sessionIDFormatter.format(new Date(System.currentTimeMillis()));
    }

    public String getGroups()
    {
        return groups;
    }

    public void setGroups(String groups)
    {
        this.groups = groups;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getStarted()
    {
        return started;
    }

    public void setStarted(String started)
    {
        this.started = started;
    }

    public Map<String, Object> getUsers()
    {
        return users;
    }

    public void setUsers(Map<String, Object> users)
    {
        this.users = users;
    }
}
