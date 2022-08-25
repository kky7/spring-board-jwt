package com.example.board.service;

import com.example.board.dto.ResponseDto;
import com.example.board.dto.UserLoginDto;
import com.example.board.dto.UserSignupDto;
import com.example.board.entity.Users;
import com.example.board.repository.UserRepository;
import com.example.board.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private  boolean isInUserCheck(String username){
        Optional<Users> findUser = userRepository.findByUsername(username);

        return findUser.isEmpty();
    }

    private boolean userStrCheck (String username){
        return Pattern.matches("^[a-zA-Z0-9]{4,12}$", username);
    }

    private boolean passwordStrCheck(String password){
        return Pattern.matches("^[a-z0-9]{4,32}$", password);
    }

    private  boolean isSamePassword(String password, String ConfirmPassword){
        return password.equals(ConfirmPassword);
    }

     // login
//    @Transactional
//    public ResponseDto<?> login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request){
//        return ResponseDto.success(null);
//    }

    //회원가입
    @Transactional
    public ResponseDto<?> signup(UserSignupDto userSignupDto) {
        String username = userSignupDto.getUsername();
        UserLoginDto userLoginDto = new UserLoginDto();

        String password = userSignupDto.getPassword();
        String ConfirmPassword = userSignupDto.getPasswordConfirm();
//        System.out.println(password);

        if (!isInUserCheck(username)) {return ResponseDto.fail("NICKNAME EXISTS", "user name is duplicated");}

        else if (!userStrCheck(username)) {return ResponseDto.fail("WRONG NICKNAME", "user name must consist of 4 or more, 12 or less alphabetic uppercase and lowercase letters(a~z, A~Z) and numbers (0~9)");}

        else if (!passwordStrCheck(password)) {return ResponseDto.fail("WRONG PASSWORD", "password must consist of 4 or more, 32 or less alphabetic lowercase letters(a~z) and numbers (0~9)");}

        else if (!isSamePassword(password, ConfirmPassword)) {return ResponseDto.fail("CHECK PASSWORD", "password and confirm password are not the same.");}

        else {
            userLoginDto.setPassword(passwordEncoder.encode(password));
            userLoginDto.setUsername(username);

            Users user = new Users(userLoginDto);

            userRepository.save(user);

            return ResponseDto.success(user);
        }
    }

    @Transactional
    public ResponseDto<?> logout(UserDetailsImpl userDetails){
        Users user = userDetails.getUser();
        user.setRefreshToken(null);
        return ResponseDto.success(null);
    }
}
