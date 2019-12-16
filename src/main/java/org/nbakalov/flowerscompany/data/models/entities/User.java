package org.nbakalov.flowerscompany.data.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;


@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseEntity implements UserDetails {

  @Column(name = "username", nullable = false, updatable = false, unique = true)
  @Size(min = 3, max = 20)
  private String username;

  @Column(name = "password", nullable = false)
  @Size(min = 6, max = 15)
  private String password;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "profile_picture_url", nullable = false)
  private String profilePictureUrl;

  @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
  @JoinTable(
          name = "user_roles",
          joinColumns = @JoinColumn(
                  name = "user_id",
                  referencedColumnName = "id"
          ),
          inverseJoinColumns = @JoinColumn(
                  name = "role_id",
                  referencedColumnName = "id"
          )
  )
  private Set<Role> authorities;

  @Override
  @Transient
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @Transient
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @Transient
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @Transient
  public boolean isEnabled() {
    return true;
  }
}
