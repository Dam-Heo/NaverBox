package com.zerobase.naverbox.service;

import com.zerobase.naverbox.entity.File;
import com.zerobase.naverbox.entity.User;
import com.zerobase.naverbox.repository.FileRepository;
import com.zerobase.naverbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String filePath;

//    public List fileList(){
//        List<File> list = new ArrayList<>();
//        return list;
//    }
//
//    public List fileList(){
//        List<File> list = new ArrayList<>();
//        return list;
//    }

    //폴더생성
    public void folderCreate(Long userId, Long id) {
        Optional<User> user = userRepository.findById(userId);
        try{
            Files.createDirectories(Paths.get(filePath + "/"+ userId));
            File file = File.builder()
                    .fileFolderDiv(1)
                    .filePath(filePath + "/" + userId)
                    .fileStatus(1)
                    .insert_dt(LocalDateTime.now())
                    .fileName(user.get().getUserId())
                    .user(user.get())
                    .build();
            fileRepository.save(file);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //파일 업로드
    public void fileUpload(List<MultipartFile> multipartFile, Long id) {
        Optional<User> user = userRepository.findById(id);
        try{
            for(MultipartFile file : multipartFile) {
                if(fileRepository.existsById(id)) {
                    throw new RuntimeException("중복된 파일이 있습니다.");
                }
                Files.copy(file.getInputStream(), Paths.get(filePath + "/" + user.get().getUserId() + "/" + file.getOriginalFilename()));
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //파일 다운로드
    public Resource loadFile(Long id){
        File file = fileRepository.findById(id).orElse(null);
        String fileName = file != null ? file.getFileName() : null;
//        Principal principal = null;
//        principal.getName();
        try{
            if(fileName == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Path filePath2 = Paths.get(filePath + "/" + "hd504" + "/" + fileName);
            Resource resource = new UrlResource(filePath2.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    //파일 리스트 조회
    @Transactional(readOnly = true)
    public List<File> findAll() {
        return fileRepository.findAll();
    }

    //파일 ID로 조회
    @Transactional(readOnly = true)
    public File findById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            fileRepository.deleteById(id);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
