package com.zerobase.naverbox.handler;

import com.zerobase.naverbox.dto.CustomOAuth2User;
import com.zerobase.naverbox.dto.UserLoginDTO;
import com.zerobase.naverbox.entity.User;
import com.zerobase.naverbox.service.JwtService;
import com.zerobase.naverbox.service.SecurityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;


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
        GrantedAuthority auth = authorities.iterator().next();
        String role = auth.getAuthority();

        User userEntity = securityService.findByUserId(userId);
        UserLoginDTO userLoginDTO = securityService.verify(userEntity);
        String accessToken = userLoginDTO.getAccessToken();
        String refreshToken = userLoginDTO.getRefreshToken();
        //Refresh Token DB에 저장
        userEntity.setRefreshToken(refreshToken);
        securityService.saveRefreshToken(userEntity);
        // JWT 토큰 전송
        String chkUserId = jwtService.extractUserId(accessToken);

//        userLoginDTO = UserLoginDTO.builder().userId(chkUserId).accessToken(accessToken).refreshToken(refreshToken).build();

        response.addCookie(createCookie("Authorization", accessToken));
        response.sendRedirect("http://localhost:8080/success");

    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
