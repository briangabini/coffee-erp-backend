package com.briangabini.coffee_erp_backend.repositories.security;

import com.briangabini.coffee_erp_backend.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
}