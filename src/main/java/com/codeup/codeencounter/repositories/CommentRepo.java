package com.codeup.codeencounter.repositories;

import com.codeup.codeencounter.models.Comment;
import com.codeup.codeencounter.models.Post;
import com.codeup.codeencounter.models.User;
import com.codeup.codeencounter.models.UserFriend;
//import javassist.util.proxy.ProxyObjectOutputStream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository <Comment, Long> {

    Comment findByBody(String body);
    List<Comment> findAllByUserId(long id);
    Comment findById(long id);
    List<Comment> findAllByParentPost(Post parentPost);
    List<Comment> findAllByUser(User user);
}
