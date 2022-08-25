package com.example.board.security.filter;

import com.example.board.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class CustomJwtAuthFilter  {
    // jwt 복호화해서 확인 - 권한부여하는 인가 필터

}
