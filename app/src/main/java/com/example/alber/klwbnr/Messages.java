package com.example.alber.klwbnr;

public class Messages {

    private String message;
    private String from;

    public Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Messages(){

    }
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}

