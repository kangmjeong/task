package aladdinsys.api.task.utils.jwt;

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
            // jwt 토큰값 꺼내기
            String jwt = jwtTokenUtil.getJwtFromRequest(request);

            if (StringUtils.isNotEmpty(jwt) && jwtTokenUtil.validateToken(jwt)) {

                String userId = jwtTokenUtil.getUserIdFromToken(jwt);

                jwtTokenUtil.setUserAuthentication(request, userId);
            } else {
                if (StringUtils.isEmpty(jwt)) {
                    request.setAttribute("unauthorization", "인증키 없음.");
                } else {

                    if (!jwtTokenUtil.validateToken(jwt)) {
                        request.setAttribute("unauthorization", "인증키 만료.");
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }
}
