package com.example.board.security;

import com.example.board.repository.UserRepository;
import com.example.board.security.filter.CustomJwtAuthFilter;
import com.example.board.security.filter.LoginFilter;
import com.example.board.security.provider.LoginAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        return (web) -> web.ignoring()
                .antMatchers("/h2-console/**");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public LoginAuthProvider loginAuthProvider() {
        return new LoginAuthProvider(encodePassword());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(loginAuthProvider());
    }

    @Bean
    public LoginSuccessHandler formLoginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    // LoginFilter -> LoginAuthProvider -> LoginFilter -> LoginSuccessHandler -> JWT 토큰 생성 -> 응답 헤더에 넣어줌
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter(authenticationManager());
        loginFilter.setFilterProcessesUrl("/board/member/login");
        loginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler());
        loginFilter.afterPropertiesSet();
        return loginFilter;
    }
//dd

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
        // 서버에서 인증은 JWT로 인증하기 때문에 Session의 생성을 막습니다.
                .addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomJwtAuthFilter(authenticationManager(),userRepository), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .httpBasic().disable()
                .authorizeRequests((authz)->authz
                        .antMatchers("/board/auth/**", "board/member/**").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling();
    }

}
