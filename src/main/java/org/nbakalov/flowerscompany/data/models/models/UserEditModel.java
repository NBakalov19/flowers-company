package org.nbakalov.flowerscompany.data.models.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserEditModel {

  private String username;
  private String oldPassword;
  private String password;
  private String confirmPassword;
  private String fullName;
  private String email;
}
