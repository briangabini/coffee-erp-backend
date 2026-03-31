package com.briangabini.coffee_erp_backend.bootstrap;

import com.briangabini.coffee_erp_backend.domain.security.Authority;
import com.briangabini.coffee_erp_backend.domain.security.Role;
import com.briangabini.coffee_erp_backend.domain.security.User;
import com.briangabini.coffee_erp_backend.repositories.security.AuthorityRepository;
import com.briangabini.coffee_erp_backend.repositories.security.RoleRepository;
import com.briangabini.coffee_erp_backend.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (authorityRepository.count() == 0) {
            loadSecurityData();
        }
    }

    private void loadSecurityData() {
        // Create Granular Authorities
        Authority createBean = authorityRepository.save(Authority.builder().permission("coffeebean.create").build());
        Authority readBean = authorityRepository.save(Authority.builder().permission("coffeebean.read").build());
        Authority updateBean = authorityRepository.save(Authority.builder().permission("coffeebean.update").build());
        Authority deleteBean = authorityRepository.save(Authority.builder().permission("coffeebean.delete").build());

        Authority readInventory = authorityRepository.save(Authority.builder().permission("inventory.read").build());
        Authority createInventory = authorityRepository.save(Authority.builder().permission("inventory.create").build());

        Authority readSupplier = authorityRepository.save(Authority.builder().permission("supplier.read").build());
        Authority createSupplier = authorityRepository.save(Authority.builder().permission("supplier.create").build());

        // Create Roles
        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role baristaRole = roleRepository.save(Role.builder().name("BARISTA").build());

        // Assign Authorities to Roles
        adminRole.setAuthorities(new HashSet<>(Set.of(
                createBean, readBean, updateBean, deleteBean,
                readInventory, createInventory,
                readSupplier, createSupplier
        )));

        baristaRole.setAuthorities(new HashSet<>(Set.of(
                readBean, readInventory
        )));

        roleRepository.saveAll(Arrays.asList(adminRole, baristaRole));

        // Create Users and Assign Roles
        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(adminRole)
                .build());

        userRepository.save(User.builder()
                .username("barista")
                .password(passwordEncoder.encode("coffee123"))
                .role(baristaRole)
                .build());

        log.info("Security Data Loaded: {} Users initialized.", userRepository.count());
    }
}