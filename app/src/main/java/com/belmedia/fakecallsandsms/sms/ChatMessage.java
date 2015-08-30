package com.belmedia.fakecallsandsms.sms;

/**
 * Created by B.E.L on 30/08/2015.
 */
public class ChatMessage {
    private long id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;
    private String hourTime;

    private String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public ChatMessage(String dateTime, String hourTime, String message, String thumbnail, boolean isMe) {
        this.dateTime = dateTime;
        this.hourTime = hourTime;
        this.message = message;
        this.thumbnail = thumbnail;
        this.isMe = isMe;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getHourTime() {
        return hourTime;
    }

    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}