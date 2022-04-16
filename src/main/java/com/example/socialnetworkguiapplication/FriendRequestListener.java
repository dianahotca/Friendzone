package com.example.socialnetworkguiapplication;

import domain.Friendship;

public interface FriendRequestListener {
    void onFriendRequestAccepted(Friendship friendship);
}
