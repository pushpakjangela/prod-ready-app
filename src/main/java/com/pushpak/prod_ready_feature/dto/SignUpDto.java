package com.pushpak.prod_ready_feature.dto;

import com.pushpak.prod_ready_feature.enums.Permission;
import com.pushpak.prod_ready_feature.enums.Role;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignUpDto {
    private String name;
    private String email;
    private String password;
    private Set<Role> roles;
    private Set<Permission> permissions;
}
