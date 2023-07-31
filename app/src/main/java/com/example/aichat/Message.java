package com.example.aichat;

public class Message
{
    public static  String  SENT_BY_ME="me";
    public static String SENT_BY_BOT="bot";
    String message;
    String sentBY;

    public Message(String message, String sentBY) {
        this.message = message;
        this.sentBY = sentBY;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBY() {
        return sentBY;
    }

    public void setSentBY(String sentBY) {
        this.sentBY = sentBY;
    }
}

