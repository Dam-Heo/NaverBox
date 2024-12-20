package com.zerobase.naverbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_upload_tb")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_pk")
    private Long id;

    @Column(name = "up_file_pk")
    private Long upFilePk;

    @Column(name = "file_folder_div")
    private int fileFolderDiv;

    @Column(name = "file_status", nullable = false)
    private int fileStatus;

    @Column(name = "partition_no", nullable = false)
    private int partitionNo;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_ext", nullable = false)
    private String fileExt;

    @Column(name = "file_size", nullable = false, columnDefinition = "decimal(10,2)")
    private long fileSize;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_upload_dt", nullable = false)
    private LocalDateTime insertDt;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private LocalDateTime deleteAt;

    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;

    @Builder
    public File(Long id, Long upFilePk, int fileFolderDiv, int fileStatus, int partitionNo, String fileName, String fileExt, long fileSize, String filePath,  User user) {
        this.id = id;
        this.upFilePk = upFilePk;
        this.fileFolderDiv = fileFolderDiv;
        this.fileStatus = fileStatus;
        this.partitionNo = partitionNo;
        this.fileName = fileName;
        this.fileExt = fileExt;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.insertDt = LocalDateTime.now();
        this.createAt = LocalDateTime.now();
        this.user = user;
    }

}
