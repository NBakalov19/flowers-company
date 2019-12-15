package org.nbakalov.flowerscompany.data.models.models.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class UserCreateModel {

  private String username;
  private String password;
  private String confirmPassword;
  private MultipartFile image;
  private String email;
}
