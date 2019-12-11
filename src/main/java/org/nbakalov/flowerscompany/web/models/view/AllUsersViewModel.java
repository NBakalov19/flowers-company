package org.nbakalov.flowerscompany.web.models.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class AllUsersViewModel {

  private String id;
  private String username;
  private String email;
  private String fullName;
  private Set<String> authorities;
}
