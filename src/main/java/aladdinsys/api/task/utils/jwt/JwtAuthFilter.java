package aladdinsys.api.task.utils.jwt;

import aladdinsys.api.task.service.UserService;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // jwt 토큰값 깨내기.
            String jwt = jwtTokenUtil.getJwtFromRequest(request);

            if (StringUtils.isNotEmpty(jwt) && jwtTokenUtil.validateToken(jwt)) {
                // 토큰에서 userId 추출.
                String userId = jwtTokenUtil.getUserIdFromToken(jwt); //jwt에서 사용자 id를 꺼낸다.
                String claims = jwtTokenUtil.getClaimFromToken(jwt, Claims::getId);

                jwtTokenUtil.setUserAuthentication(request, userId);

            } else {
                if (StringUtils.isEmpty(jwt)) {
                    request.setAttribute("unauthorization", "401 인증키 없음.");
                }
                if (jwtTokenUtil.validateToken(jwt)) {
                    request.setAttribute("unauthorization", "401-001 인증키 만료.");
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
//            throw new Exception("인증서 정보를 확인해주세요.");
        }

        filterChain.doFilter(request, response);
    }
}
