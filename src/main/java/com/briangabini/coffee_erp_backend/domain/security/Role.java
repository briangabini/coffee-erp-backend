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
public class Role {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "role_authority",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id")})
    private Set<Authority> authorities = new HashSet<>();
}
