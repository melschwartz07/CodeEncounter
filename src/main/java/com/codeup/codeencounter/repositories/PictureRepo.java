package com.codeup.codeencounter.repositories;

import com.codeup.codeencounter.models.Gallery;
import com.codeup.codeencounter.models.Picture;
import com.codeup.codeencounter.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepo extends JpaRepository <Picture, Long> {

    Picture findById(long id);
    List<Picture> findAllByUser(User user);
    List<Picture> findAllByGallery(Gallery gallery);
    Picture findByComment(String comment);
}
