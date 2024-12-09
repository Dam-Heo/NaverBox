package com.zerobase.naverbox.repository;

import com.zerobase.naverbox.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    boolean existsById(Long id);
}
