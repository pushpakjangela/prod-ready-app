package com.pushpak.prod_ready_feature.utils;

import com.pushpak.prod_ready_feature.enums.Permission;
import com.pushpak.prod_ready_feature.enums.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pushpak.prod_ready_feature.enums.Permission.*;
import static com.pushpak.prod_ready_feature.enums.Role.*;

public class PermissionMapping {

    private static final Map<Role, Set<Permission>> map = Map.of(
            ADMIN, Set.of(POST_CREATE,POST_DELETE,POST_UPDATE),
            CREATOR, Set.of(POST_CREATE),
            USER, Set.of(USER_VIEW,POST_VIEW)
    );

    public static Set<SimpleGrantedAuthority> getAuthorities(Role role) {
        return map.get(role).stream().map(permission -> new SimpleGrantedAuthority(permission.name())).collect(Collectors.toSet());
    }
}
