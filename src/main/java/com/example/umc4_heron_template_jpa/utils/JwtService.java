package com.example.umc4_heron_template_jpa.utils;

import com.example.umc4_heron_template_jpa.config.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Date;

import static com.example.umc4_heron_template_jpa.config.BaseResponseStatus.EMPTY_JWT;
import static com.example.umc4_heron_template_jpa.config.BaseResponseStatus.INVALID_JWT;

@Service
public class JwtService {
    /**
     * Jwt생성
     */
    public String createJwt(Long memberId){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("memberId",memberId)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("Authorization");
    }

    /*
    JWT에서 userIdx 추출
    @return Long
    @throws BaseException
     */
    public Long getUserIdx() throws BaseException {
        //1. JWT 추출
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }
        // 3. memberId 추출
        return claims.getBody().get("memberId",Long.class);  // jwt 에서 memberId를 추출합니다.
    }

}
