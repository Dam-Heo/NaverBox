package com.zerobase.naverbox.controller;

import com.zerobase.naverbox.dto.FileDTO;
import com.zerobase.naverbox.entity.File;
import com.zerobase.naverbox.entity.User;
import com.zerobase.naverbox.service.FileService;
import com.zerobase.naverbox.service.SecurityService;
import com.zerobase.naverbox.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileController {

    private final FileService fileService;
    private final SecurityService securityService;

    @GetMapping("/files")
    public ResponseEntity<List<File>> fileList(Authentication authentication,
            @RequestParam(defaultValue = "0") int page, //현재 페이지
            @RequestParam(defaultValue = "10") int size //크기
        ){
        String userId = authentication.getName();
        User user = securityService.findByUserId(userId);
        Page<File> files = fileService.findAllByUser_IdOrderByInsertDtDesc(PageRequest.of(page, size), user.getId()); // <File>
        return new ResponseEntity(files, HttpStatus.OK);
    }

//    @PostMapping("/fileSearch/{fileName}")
//    public ResponseEntity fileSearch(@PathVariable String fileName){
//        return new ResponseEntity("성공", HttpStatus.OK);
//    }

    @PostMapping("/fileUpload")
    public ResponseEntity fileUpload(@RequestParam("file") List<MultipartFile> multipartFiles
            , Authentication authentication){
        String userId = authentication.getName();
        User user = securityService.findByUserId(userId);
        Long id = user.getId();
        if(multipartFiles.isEmpty()){
            throw new RuntimeException("파일이 없습니다.");
        }
        if (id == null) {
            throw new RuntimeException("사용자이름이 없습니다.");
        }
        fileService.fileUpload(multipartFiles, id);
        return new ResponseEntity("성공", HttpStatus.OK);
    }

    @GetMapping("/fileDownload/{id}")
    public ResponseEntity fileDownload(@PathVariable Long id, HttpServletRequest request) throws IOException {
        Resource resource = fileService.loadFile(id);
        String path = resource.getFile().getAbsolutePath();
        String contentType = request.getServletContext().getMimeType(path);
        if(!StringUtils.hasText(contentType)){
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/fileDelete/{id}")
    public ResponseEntity fileDelete(@PathVariable Long id) throws IOException {
        String url = fileService.findById(id).getFilePath();
        Path filePath = Paths.get(url);
        Files.delete(filePath);
        fileService.deleteById(id);
        return new ResponseEntity("삭제성공", HttpStatus.OK);
    }

//    @PostMapping("/fileSizeChk")
//    public ResponseEntity fileSizeChk(MultipartFile multipartFile){
//        return new ResponseEntity("성공", HttpStatus.OK);
//    }

    @PostMapping("/folderCreate")
    public ResponseEntity folderCreate(@RequestParam("id") Long id, Authentication authentication){
        String userId = authentication.getName();
        User user = securityService.findByUserId(userId);
        Long userIdx = user.getId();
        fileService.folderCreate(userIdx, id);
        return new ResponseEntity("성공", HttpStatus.OK);
    }

//    @PostMapping("/fileShare")
//    public ResponseEntity fileShare(File file){
//        return new ResponseEntity("성공", HttpStatus.OK);
//    }
}
