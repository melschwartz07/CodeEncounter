package com.codeup.codeencounter.repositories;

import com.codeup.codeencounter.models.Gallery;
import com.codeup.codeencounter.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryRepo extends JpaRepository <Gallery, Long> {

    Gallery findById(long id);
    Gallery findByName(String name);
    List<Gallery> findAllByUser(User user);

}
