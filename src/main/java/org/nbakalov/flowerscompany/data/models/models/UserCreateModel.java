package org.nbakalov.flowerscompany.data.models.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserCreateModel {

  private String username;
  private String password;
  private String confirmPassword;
  private String fullName;
  private String email;
}
