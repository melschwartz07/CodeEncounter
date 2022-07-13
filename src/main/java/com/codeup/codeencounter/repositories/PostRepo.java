package com.codeup.codeencounter.repositories;

import com.codeup.codeencounter.models.Post;
import com.codeup.codeencounter.models.User;
import com.codeup.codeencounter.models.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepo extends JpaRepository <Post, Long> {
    Post findById(long id);
    List<Post> findAllByUserUsername(String username);
    List<Post> findAllByUser(User user);
    Post findByBody(String body);
}
