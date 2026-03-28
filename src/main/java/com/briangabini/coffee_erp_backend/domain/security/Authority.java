package com.briangabini.coffee_erp_backend.domain.security;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Authority {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String permission;

    @Builder.Default
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles = new HashSet<>();
}
