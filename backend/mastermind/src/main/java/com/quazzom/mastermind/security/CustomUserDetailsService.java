package com.quazzom.mastermind.security;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByNickname(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new CustomUserDetails(user);
    }

	public UserDetails loadUserByUuidPublic(UUID uuidPublic) throws UsernameNotFoundException {
		User user = userRepository.findByUuidPublic(uuidPublic)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

		return new CustomUserDetails(user);
	}
}