package com.zerobase.naverbox.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attribute;
    private final Map<String, Object> attribute2;
    private final Map<String, Object> attribute3;

    // 네이버 JSON(response 안에 id와 name이 존재)
    //		{resultcode=00, message=success, response={id=123123123, name=개발자유미}}
    public KakaoResponse(Map<String, Object> attribute) {

        this.attribute = attribute;
        this.attribute2 = (Map<String, Object>) attribute.get("kakao_account");
        this.attribute3 = (Map<String, Object>) attribute2.get("profile");
    }

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return attribute2.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute3.get("nickname").toString();
    }
}
