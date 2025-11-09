package com.zhiye.backend.security;

import com.zhiye.backend.model.entity.User;
import com.zhiye.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrId) throws UsernameNotFoundException {
        User user = null;
        try {
            Long id = Long.parseLong(usernameOrId);
            user = userRepository.findById(id).orElse(null);
        } catch (NumberFormatException ignored) {
            user = userRepository.findByUsername(usernameOrId).orElse(null);
        }
        if (user == null) throw new UsernameNotFoundException("user not found");
        return  org.springframework.security.core.userdetails.User.withUsername(String.valueOf(user.getId()))
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .accountExpired(false).accountLocked(false).credentialsExpired(false).disabled(false)
                .build();
    }
}
