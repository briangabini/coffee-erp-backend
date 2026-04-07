package com.briangabini.coffee_erp_backend.security;

import com.briangabini.coffee_erp_backend.domain.security.Authority;
import com.briangabini.coffee_erp_backend.domain.security.Role;
import com.briangabini.coffee_erp_backend.domain.security.User;
import com.briangabini.coffee_erp_backend.repositories.security.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("JpaUserDetails Service Unit Tests")
public class JpaUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JpaUserDetailsService jpaUserDetailsService;

    @Test
    void shouldLoadUserMapAndAuthoritiesSuccessfully() {

        // given
        Authority readAuth = Authority.builder().permission("coffeebean.read").build();
        Role baristaRole = Role.builder().name("BARISTA").authorities(Set.of(readAuth)).build();
        User mockDbUser = User.builder()
                .username("barista")
                .password("encoded_pass")
                .roles(Set.of(baristaRole))
                .build();
        given(userRepository.findByUsername("barista")).willReturn(Optional.of(mockDbUser));

        // when
        UserDetails springSecurityUser = jpaUserDetailsService.loadUserByUsername("barista");

        // then
        assertThat(springSecurityUser.getUsername()).isEqualTo(mockDbUser.getUsername());
        assertThat(springSecurityUser.getPassword()).isEqualTo(mockDbUser.getPassword());
        assertThat(springSecurityUser.getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .containsExactly("coffeebean.read");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        // given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> jpaUserDetailsService.loadUserByUsername("ghost_user"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: ghost_user");
    }

}
