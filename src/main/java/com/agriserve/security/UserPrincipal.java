package com.agriserve.security;

import com.agriserve.entity.User;
import com.agriserve.entity.enums.Status;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security principal wrapping the AgriServe User entity.
 * Role is prefixed with "ROLE_" as required by Spring Security.
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String name;
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long userId, String name, String email, String password,
                         boolean active, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        return new UserPrincipal(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getStatus() == Status.ACTIVE,
                authorities
        );
    }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }
}
