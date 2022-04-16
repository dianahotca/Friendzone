package domain;

import utils.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Message extends Entity<Long> {

    private Long id;
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private Message reply;

    public Message(Long id, User from, List<User> to, String message, Message reply, String date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.parse(date,Constants.DATE_TIME_FORMATTER);
        this.reply = reply;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {

        String to1 = "";
        for(User user : to){
            to1 = to1 + user.getFirstName() + " " + user.getLastName();
            to1 = to1 + " , ";
        }


        return " date = " + date.format(Constants.DATE_TIME_FORMATTER) +
                "| from = " + from.getFirstName() + " " + from.getLastName() +
                "| to = " + to1 +
                "| message =  '" + message + '\'' ;
    }
}