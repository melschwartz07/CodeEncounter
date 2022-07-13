package com.codeup.codeencounter.repositories;

import com.codeup.codeencounter.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository <User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}
