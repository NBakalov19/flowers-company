package org.nbakalov.flowerscompany.web.models.view.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserProfileViewModel {

  private String username;
  private String email;
  private String profilePictureUrl;
}
