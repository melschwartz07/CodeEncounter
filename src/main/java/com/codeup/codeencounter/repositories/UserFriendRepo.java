package com.codeup.codeencounter.repositories;

import com.codeup.codeencounter.models.Status;
import com.codeup.codeencounter.models.User;
import com.codeup.codeencounter.models.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFriendRepo extends JpaRepository <UserFriend, Long> {
    List<UserFriend> findAllByUser(User user);
    List<UserFriend> findAllByFriend(User friend);
    List<UserFriend> findAllByFriendAndStatus(User friend, Status status);
    List<UserFriend> findAllByUserAndStatus(User user, Status status);
    UserFriend findByUserAndFriend(User user, User friend);

    UserFriend findByUserAndFriendAndStatus(User user, User user1, Status status);
    UserFriend findByFriendAndUserAndStatus(User user, User user1, Status status);
}
