package aladdinsys.api.task.utils.jwt;

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
            String jwt = jwtTokenUtil.getJwtFromRequest(request);
            if (StringUtils.isEmpty(jwt)) {
                request.setAttribute("unauthorization", "인증키 없음.");
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtTokenUtil.validateToken(jwt)) {
                request.setAttribute("unauthorization", "인증키 만료.");
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtTokenUtil.getUserIdFromToken(jwt);
            jwtTokenUtil.setUserAuthentication(request, userId);
        } catch (Exception ex) {
            logger.error("사용자 인증을 설정할 수 없습니다.", ex);
        }
        filterChain.doFilter(request, response);
    }
}
