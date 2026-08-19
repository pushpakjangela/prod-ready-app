package com.pushpak.prod_ready_feature.services;

import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.enums.Messages;
import com.pushpak.prod_ready_feature.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class  UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getByEmail(username)
                .orElseThrow(
                        ()-> new BadCredentialsException(String.format(Messages.USER_NOT_FOUND_WITH_EMAIL.getMessage(),username
                        ))
                );
    }



    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(Messages.USER_NOT_FOUND_WITH_EMAIL.getMessage()));
    }
}
