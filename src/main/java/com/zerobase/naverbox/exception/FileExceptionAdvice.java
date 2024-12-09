package com.zerobase.naverbox.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.nio.file.FileAlreadyExistsException;

@ControllerAdvice
public class FileExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleFileException(MaxUploadSizeExceededException e) {
        return ResponseEntity.badRequest().body("파일이 너무 큽니다.");
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<String> handleFileException(FileAlreadyExistsException e) {
        return ResponseEntity.badRequest().body("파일이 이미 존재합니다.");
    }
}
