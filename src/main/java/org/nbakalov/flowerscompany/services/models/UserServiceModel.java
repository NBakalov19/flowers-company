package org.nbakalov.flowerscompany.services.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class UserServiceModel extends BaseServiceModel {

  private String username;
  private String password;
  private String email;
  private String profilePictureUrl;
  private Set<RoleServiceModel> authorities;

}
