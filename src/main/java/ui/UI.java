package ui;

import domain.Entity;

public interface UI<ID,E extends Entity<ID>, ID2, E2 extends Entity<ID2>,ID3 ,E3 extends Entity<ID3>> {

    void startUI();
    void printAll();
    void logIn();
    void addUser();
    void removeUser();
    void addFriend();
    void removeFriend();
    void numberOfCommunities();
    void mostSociableCommunity();
     void friendshipsByMonth();
    void findOneUsersFriends();
}
