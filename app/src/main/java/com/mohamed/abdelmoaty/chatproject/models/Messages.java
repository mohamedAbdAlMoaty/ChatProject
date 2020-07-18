package com.mohamed.abdelmoaty.chatproject.models;

/**
 * Created by HP on 6/23/2018.
 */

public class Messages {
    String message,seen,time,type,from;

    public Messages(String message, String seen, String time, String type,String from) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from=from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}