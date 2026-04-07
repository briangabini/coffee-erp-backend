package com.briangabini.coffee_erp_backend.repositories.security;

import com.briangabini.coffee_erp_backend.domain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}