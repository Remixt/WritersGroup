package csce.unt.writersgroup.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Satya on 3/28/2017.
 */

public class Session implements Serializable
{
    private String sessionId;
    private String groups;
    private String started;

    public Session()
    {
        this.sessionId = new SimpleDateFormat("MMddyyyy").format(new Date(System.currentTimeMillis()));;
        this.started = "false";
        this.groups = "";
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }
}
