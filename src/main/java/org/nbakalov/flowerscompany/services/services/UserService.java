package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
  UserServiceModel registerUser(UserServiceModel userServiceModel);

  UserServiceModel findByUsername(String username);

  UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword);

  List<UserServiceModel> findAllUsers();

  void setUserRole(String id, String role);
}
