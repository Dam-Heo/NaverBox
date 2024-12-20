package com.zerobase.naverbox.repository;

import com.zerobase.naverbox.entity.File;
import com.zerobase.naverbox.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    boolean existsById(Long id);
    Page<File> findAllByUser_IdOrderByInsertDtDesc(Pageable pageable, Long id);
}
