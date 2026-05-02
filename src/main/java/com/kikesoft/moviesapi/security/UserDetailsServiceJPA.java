package com.kikesoft.moviesapi.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kikesoft.moviesapi.entity.UserEntity;
import com.kikesoft.moviesapi.repository.UserRepository;

/**
 * JPA-backed UserDetailsService counterpart to the reference implementation.
 */
@Service
public class UserDetailsServiceJPA implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return new UserDetailsJPA(user.get());
        }

        throw new UsernameNotFoundException("User not found");
    }
}
