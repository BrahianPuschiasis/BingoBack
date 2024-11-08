package com.Sofka.BingkBack.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

import java.util.*;
import java.util.Collection;



@Component
public class KeyCloakJwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private static Collection<GrantedAuthority> extractResourceRoles(final Jwt jwt) throws JsonProcessingException {
        Set<GrantedAuthority> resourcesRoles = new HashSet<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        JsonNode claims = objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get("claims");

        resourcesRoles.addAll(extractRolesRealmAccess("realm_access", claims));

        return resourcesRoles;
    }

    private static List<GrantedAuthority> extractRolesRealmAccess(String route, JsonNode jwt) {
        Set<String> rolesWithPrefix = new HashSet<>();
        jwt.path(route)
                .path("roles")
                .elements()
                .forEachRemaining(r -> rolesWithPrefix.add("ROLE_" + r.asText()));
        return AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));
    }

    @Override
    public JwtAuthenticationToken convert(final Jwt source) {
        Collection<GrantedAuthority> authorities;
        try {
            authorities = getGrantedAuthorities(source);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            authorities = Collections.emptyList();
        }
        return new JwtAuthenticationToken(source, authorities);
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(Jwt source) throws JsonProcessingException {
        return extractResourceRoles(source);
    }
}
