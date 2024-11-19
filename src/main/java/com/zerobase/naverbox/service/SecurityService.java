package com.zerobase.naverbox.service;

import com.zerobase.naverbox.dto.UserLoginDTO;
import com.zerobase.naverbox.entity.User;
import com.zerobase.naverbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SecurityService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationContext context;
    final AuthenticationManager authenticationManager;

    //회원가입
    public void save(User user) {
        user = User.builder()
                .userId(user.getUserId())
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .name(user.getName())
                .userRole(user.getUserRole())
                .refreshToken(user.getRefreshToken())
                .build();
        userRepository.save(user);
    }

    //비밀번호 확인
    public boolean passwordChk(String userId, String password) {
        User user = userRepository.findByUserId(userId);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    //아이디로 정보 가져오기
    public User findByUserId(String userId) {
        User user = userRepository.findByUserId(userId);
        return user;
    }

    //아이디중복체크
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    //RefreshToken 저장
    @Transactional
    public void saveRefreshToken(User user) {
        user = User.updateRefreshTokenBuilder()
                .id(user.getId())
                .refreshToken(user.getRefreshToken())
                .build();
    }

    //AccessToken 재발급
    public String updateRefreshToken(String userId) throws Exception{
        User user = userRepository.findByUserId(userId);
        UserDetails userDetails = context.getBean(UserService.class).loadUserByUsername(user.getUserId());
        if(user == null) {
            throw new Exception("사용자가 존재하지 않습니다.");
        }
        boolean valid = jwtService.validateToken(user.getRefreshToken(), userDetails);
        if(!valid) {
            throw new Exception("리프레시토큰이 일치하지 않습니다.");
        }
        return jwtService.createAccessToken(user.getUserId(), user.getUserRole().name());
    }

    public UserLoginDTO verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword()));
        UserLoginDTO UserLoginDTO;
        if (authentication.isAuthenticated()) {
            return UserLoginDTO = jwtService.generateToken(user.getUserId(), user.getUserRole().name());
        } else {
            return null;
        }
    }
}
