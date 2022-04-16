package domain;

import com.example.socialnetworkguiapplication.FriendRequestListener;
import javafx.stage.Stage;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendRequest extends Entity<Tuple<String,String>>{
    private String status;
    private String date;
    private List<FriendRequestListener> listeners=new ArrayList<>();

    public FriendRequest(String status, Tuple<String,String> userIds,String date) {
        this.status = status;
        this.date=date;
        this.setId(userIds);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if(status.equals("approved"))
            for (FriendRequestListener listener:listeners)
                listener.onFriendRequestAccepted(new Friendship(this.getId(), LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(status, that.status) && Objects.equals(getId(), that.getId()) && Objects.equals(date,that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, getId());
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "Status='" + status + '\'' +
                ", userEmails=" + getId() +
                ", sent on="+ getDate() +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addListener(FriendRequestListener listener){
        listeners.add(listener);
    }
}
