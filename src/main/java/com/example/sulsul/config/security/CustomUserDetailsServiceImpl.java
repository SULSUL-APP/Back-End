package com.example.sulsul.config.security;

import com.example.sulsul.exception.BaseException;
import com.example.sulsul.exception.user.UserNotFoundException;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("[CustomUserDetailsServiceImpl] loadUserByUsername -> username: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException());

        return new UserAccount(user);
    }

}
