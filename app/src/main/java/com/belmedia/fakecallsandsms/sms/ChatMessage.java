package com.belmedia.fakecallsandsms.sms;

/**
 * Created by B.E.L on 30/08/2015.
 */
public class ChatMessage {
    private boolean isMe;
    private String message;
    private String dateTime;
    private String hourTime;
    private String thumbnail;

    public String getSenderNumber() {
        return senderNumber;
    }

    private String senderNumber;

    public String getThumbnail() {
        return thumbnail;
    }

    public ChatMessage(String senderNumber, String dateTime, String hourTime, String message, String thumbnail, boolean isMe) {
        this.senderNumber = senderNumber;
        this.dateTime = dateTime;
        this.hourTime = hourTime;
        this.message = message;
        this.thumbnail = thumbnail;
        this.isMe = isMe;
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

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}