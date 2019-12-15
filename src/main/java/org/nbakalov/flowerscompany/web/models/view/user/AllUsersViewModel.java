package org.nbakalov.flowerscompany.web.models.view.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class AllUsersViewModel {

  private String id;
  private String profilePictureUrl;
  private String username;
  private String email;
  private Set<String> authorities;
}
