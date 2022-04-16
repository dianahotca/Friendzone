package com.example.socialnetworkguiapplication;

public class FriendRequestSentModel {
    private String toUserEmail;
    private String status;
    private String date;

    public FriendRequestSentModel(String toUserEmail, String status, String date) {
        this.toUserEmail = toUserEmail;
        this.status = status;
        this.date = date;
    }

    public String getToUserEmail() {
        return toUserEmail;
    }

    public void setToUserEmail(String toUserEmail) {
        this.toUserEmail = toUserEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
