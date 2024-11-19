package com.zerobase.naverbox.handler;

import com.zerobase.naverbox.dto.CustomOAuth2User;
import com.zerobase.naverbox.dto.UserLoginDTO;
import com.zerobase.naverbox.entity.User;
import com.zerobase.naverbox.service.JwtService;
import com.zerobase.naverbox.service.SecurityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final SecurityService securityService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, IOException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String userId = customUserDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        User userEntity = securityService.findByUserId(userId);
        //JWT 토큰 생성
        UserLoginDTO userLoginDTO = securityService.verify(userEntity);
        String accessToken = userLoginDTO.getAccessToken();
        String refreshToken = userLoginDTO.getRefreshToken();


        //Refresh Token DB에 저장
        userEntity.setRefreshToken(refreshToken);
        securityService.saveRefreshToken(userEntity);
        // JWT 토큰 전송
        String chkUserId = jwtService.extractUserId(accessToken);

        userLoginDTO = UserLoginDTO.builder().userId(chkUserId).accessToken(accessToken).refreshToken(refreshToken).build();

        response.getWriter().print(userLoginDTO);
    }
}
