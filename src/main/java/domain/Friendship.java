package domain;

import com.example.socialnetworkguiapplication.FriendRequestListener;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Tuple<String, String>> {
    private Tuple<String, String> userEmails;
    private String date;

    public Friendship(Tuple<String, String> userEmailsTuple, String date) {
        this.userEmails = userEmailsTuple;
        this.date = date;
        this.setId(userEmailsTuple);
    }

    public Tuple<String, String> getUserEmails() {
        return userEmails;
    }

    public String getDate(){
        return this.date;
    }

    public void setDate(){
        this.date = LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
    }

    public void setUserEmails(Tuple<String, String> userEmails) {
        this.userEmails = userEmails;
        this.setId(getUserEmails());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(userEmails, that.userEmails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userEmails);
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "user emails=" + userEmails +
                " date=" + date +
                '}';
    }
}
