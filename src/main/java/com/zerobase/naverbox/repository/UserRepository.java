package com.zerobase.naverbox.repository;

import com.zerobase.naverbox.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String userId);

    User findByUserId(String userId);
}
