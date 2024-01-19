package aladdinsys.api.task.utils;

import aladdinsys.api.task.utils.jwt.JwtAuthFilter;
import aladdinsys.api.task.utils.jwt.JwtAuthenticationEntryPoint;
import aladdinsys.api.task.utils.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt; //salt는 암호화해줄 문자열에 임의의 문자열을 덧붙이는 것을 의미

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtTokenUtil jwtTokenUtil) {
        return new JwtAuthFilter(jwtTokenUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtTokenUtil);
        http
                .cors(cors->cors.disable())  // CORS 비활성화
                .csrf(csrf->csrf.disable()) // 토큰 사용하는 방식으로 CSRF 비활성화
                .formLogin(formLogin->formLogin.disable()) // 기본 로그인 페이지 비활성화
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // H2 콘솔과 같은 프레임/iframe 사용을 위해 비활성화
                .authorizeRequests(authorizeRequests -> authorizeRequests
                                .requestMatchers("/h2-console/*", "/szs/signup", "/swagger-ui.html", "/szs/addAllowedUser").permitAll() // 특정 경로 허용
                                .requestMatchers("/**").authenticated()) //나머지 요청은 인증 필요
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AesBytesEncryptor aesBytesEncryptor() {return new AesBytesEncryptor(secret, "70726574657374"); }
}
