package com.briangabini.coffee_erp_backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "suppliers")
public class Supplier extends BaseEntity {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String contactEmail;

    @Builder.Default
    @ManyToMany(mappedBy = "suppliers")
    private Set<CoffeeBean> coffeeBeans = new HashSet<>();

}
