package com.example.board.repository;

import com.example.board.entity.RefreshToken;
import com.example.board.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUsers(Users users);
}

