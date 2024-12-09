package com.zerobase.naverbox.service;

import com.zerobase.naverbox.dto.*;
import com.zerobase.naverbox.entity.User;
import com.zerobase.naverbox.entity.UserRole;
import com.zerobase.naverbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    private final UserRepository userRepository;
    //클라이언트 요청 url
    //http://localhost:8080/login/oauth2/code/서비스명

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }else if(registrationId.equals("kakao")){
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }
        String userId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        User existData = userRepository.findByUserId(userId);

        if (existData == null) {
            User user = User.builder()
                    .userId(userId)
                    .email(oAuth2Response.getEmail())
                    .snsType(oAuth2Response.getProvider())
                    .name(oAuth2Response.getName())
                    .userRole(UserRole.valueOf("USER"))
                    .build();

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userId);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setUserRole(UserRole.valueOf("USER"));

            return new CustomOAuth2User(userDTO);
        }
        else {

            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());
            existData.setUpdateAt(LocalDateTime.now());


            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(existData.getUserId());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setUserRole(existData.getUserRole());

            return new CustomOAuth2User(userDTO);
        }
    }
}
