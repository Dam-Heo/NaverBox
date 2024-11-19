package com.zerobase.naverbox.filter;

import com.zerobase.naverbox.dto.Code;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");

        try{
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e){
            //만료 에러
            request.setAttribute("exception", Code.EXPIRED_TOKEN.getCode());

        } catch (MalformedJwtException e){

            //변조 에러
            request.setAttribute("exception", Code.WRONG_TYPE_TOKEN.getCode());


        } catch (SignatureException | IOException e){
            //형식, 길이 에러
            request.setAttribute("exception", Code.WRONG_TYPE_TOKEN.getCode());
        }
        filterChain.doFilter(request, response);


    }
}
