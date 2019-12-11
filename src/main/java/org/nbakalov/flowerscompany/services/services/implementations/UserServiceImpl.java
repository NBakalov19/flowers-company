package org.nbakalov.flowerscompany.services.services.implementations;

import static org.nbakalov.flowerscompany.constants.RoleConstants.*;
import static org.nbakalov.flowerscompany.constants.UserConstants.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.nbakalov.flowerscompany.data.repositories.UserRepository;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.RoleService;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final RoleService roleService;
  private final ModelMapper modelMapper;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserServiceModel registerUser(UserServiceModel userServiceModel) {
    roleService.seedRolesInDb();

    if (userRepository.count() == 0) {
      userServiceModel.setAuthorities(roleService.findAllRoles());
    } else {
      userServiceModel.setAuthorities(new LinkedHashSet<>());
      userServiceModel.getAuthorities()
              .add(roleService.findByAuthority(ROLE_CUSTOMER));
    }

    User user = modelMapper.map(userServiceModel, User.class);
    user.setPassword(bCryptPasswordEncoder.encode(userServiceModel.getPassword()));

    return modelMapper.map(userRepository.saveAndFlush(user), UserServiceModel.class);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_MESSAGE));
  }

  @Override
  public UserServiceModel findByUsername(String username) {
    return userRepository.findByUsername(username)
            .map(user -> modelMapper.map(user, UserServiceModel.class))
            .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_MESSAGE));
  }

  @Override
  public UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword) {
    User user = userRepository.findByUsername(userServiceModel.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_MESSAGE));

    if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException(WRONG_PASSWORD_MESSAGE);
    }

    user.setPassword(userServiceModel.getPassword() != null
            ? bCryptPasswordEncoder.encode(userServiceModel.getPassword())
            : user.getPassword());

    user.setEmail(userServiceModel.getEmail());

    return modelMapper.map(userRepository.saveAndFlush(user), UserServiceModel.class);
  }

  @Override
  public List<UserServiceModel> findAllUsers() {
    return userRepository.findAll()
            .stream()
            .map(user -> modelMapper.map(user, UserServiceModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public void setUserRole(String id, String role) {

    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(WRONG_ID_MESSAGE));

    UserServiceModel userServiceModel = modelMapper.map(user, UserServiceModel.class);

    userServiceModel.getAuthorities().clear();

    if (role.equals("operator")) {
      userServiceModel.getAuthorities().add(roleService.findByAuthority(ROLE_OPERATOR));
    } else if (role.equals("admin")) {
      userServiceModel.getAuthorities().add(roleService.findByAuthority(ROLE_OPERATOR));
      userServiceModel.getAuthorities().add(roleService.findByAuthority(ROLE_ADMIN));
    }

    userRepository.saveAndFlush(modelMapper.map(userServiceModel, User.class));
  }
}
