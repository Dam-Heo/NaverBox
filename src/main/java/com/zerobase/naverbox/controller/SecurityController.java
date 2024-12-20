package com.zerobase.naverbox.controller;

import com.zerobase.naverbox.dto.UserLoginDTO;
import com.zerobase.naverbox.entity.User;
import com.zerobase.naverbox.service.JwtService;
import com.zerobase.naverbox.service.SecurityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
//@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody User user) {
        if (securityService.existsByUserId(user.getUserId())) {
            return new ResponseEntity("이미 존재하는 아이디입니다.", HttpStatus.CONFLICT);
        }else{
            securityService.save(user);
            return new ResponseEntity("회원가입이 완료되었습니다.", HttpStatus.CREATED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user, BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        User userEntity = securityService.findByUserId(user.getUserId());
        if(userEntity==null){
            return new ResponseEntity("사용자가 존재하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        if(!securityService.passwordChk(userEntity.getUserId(), user.getPassword())) {
            return new ResponseEntity("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        user.setUserRole(userEntity.getUserRole());
        //JWT 토큰 생성
        UserLoginDTO userLoginDTO = securityService.verify(user);
        String accessToken = userLoginDTO.getAccessToken();
        String refreshToken = userLoginDTO.getRefreshToken();


        //Refresh Token DB에 저장
        userEntity.setRefreshToken(refreshToken);
        securityService.saveRefreshToken(userEntity);

        // JWT 토큰 전송
        String userId = jwtService.extractUserId(accessToken);

        userLoginDTO = UserLoginDTO.builder().userId(userId).accessToken(accessToken).refreshToken(refreshToken).build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.add("Authorization-refresh", refreshToken);

        return ResponseEntity.ok().headers(headers).body("success");
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String token) {
        // token repository에서 refresh Token에 해당하는 값을 삭제한다.
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh")
    public ResponseEntity refresh(@RequestHeader("Authorization") String token) throws Exception {
        String userId = jwtService.extractUserId(token);
        String newAccessToken = securityService.updateRefreshToken(userId);
        return new ResponseEntity(newAccessToken, HttpStatus.OK);
    }
}
