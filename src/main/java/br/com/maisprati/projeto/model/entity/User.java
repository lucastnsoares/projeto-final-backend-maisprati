package br.com.maisprati.projeto.model.entity;

import br.com.maisprati.projeto.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 60, nullable = false)
    private Set<Role> role;


    @Column(length = 120, nullable = false)
    private String name;

    @Column(length = 20, nullable = false, unique = true)
    private String document;

    @Column(length = 120, nullable = false, unique = true)
    private String email;

    @Column(name="password_hash", length = 64)
    @Setter(AccessLevel.NONE)
    private String passwordHash;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
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
        return this.active;
    }

    public void setEncodedPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("A senha não pode estar em branco.");
        }
        if(rawPassword.length() < 6) {
            throw new IllegalArgumentException("A senha deve possuir no mínimo 6 caracteres.");
        }
        this.passwordHash = passwordEncoder.encode(rawPassword);
    }
}
