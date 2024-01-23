package aladdinsys.api.task.utils.jwt;


import aladdinsys.api.task.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Component
public class JwtTokenUtil implements Serializable {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration_time}")
    private long expirationTime;

    /**
     * jwt 토큰에서 userId 검색
     *
     * @param token
     * @return
     */
    public String getUserIdFromToken(String token) throws JwtException {
        try {
            final Claims claims = getClaimFromToken(token, Function.identity());
            return claims.get("userId", String.class);
        } catch (JwtException ex) {
            throw ex;
        }
    }

    /**
     * jwt 토큰에서 날짜 만료 검색
     *
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * secret 키를 가지고 토큰에서 정보 검색
     *
     * @param token
     * @return
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰 만료 체크
     *
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDTO userDTO) {
        // Claims creation
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", userDTO.name());
        claims.put("userId", userDTO.userId());

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 토큰 검증
     *
     * @param token
     * @return
     * @throws Exception
     */
    public Boolean validateToken(String token) throws JwtException {
        try {
            return !isTokenExpired(token);
        } catch (JwtException ex) {
            throw ex;
        }
    }

    /**
     * UserAuthentication 세팅
     *
     * @param request
     * @param userId
     */
    public void setUserAuthentication(HttpServletRequest request, String userId) {
        UserAuthentication authentication = new UserAuthentication(userId, null, null);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * HttpServletRequest 에서 토큰 추출
     *
     * @param request
     * @return
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}