package repository.db;

import com.example.socialnetworkguiapplication.UserModel;
import domain.Friendship;
import domain.UserDto;
import repository.paging.Page;
import repository.paging.Pageable;

public interface FriendshipPagingRepository {
    Page<UserModel> getFriends(Pageable<UserModel> pageable, String email);
}
