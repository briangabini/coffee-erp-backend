package com.briangabini.coffee_erp_backend.repositories.security;

import com.briangabini.coffee_erp_backend.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}