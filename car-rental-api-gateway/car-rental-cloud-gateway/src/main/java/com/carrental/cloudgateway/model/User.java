package com.carrental.cloudgateway.model;

import com.carrental.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Table(name = "user", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails, Persistable<Long> {

    @Id
    private Long id;
    private String username;
    private String password;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private String address;
    @Transient
    private boolean newUser;

    @Override
    @Transient
    public boolean isNew() {
        return this.newUser || id == null;
    }

    public User setAsNew() {
        this.newUser = true;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

