package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.nbakalov.flowerscompany.data.repositories.UserRepository;
import org.nbakalov.flowerscompany.errors.WrongPasswordException;
import org.nbakalov.flowerscompany.errors.dublicates.UserAllreadyExistException;
import org.nbakalov.flowerscompany.errors.dublicates.UserWithThisEmailAllreadyExist;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalOrderServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.UserNotFoundException;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.RoleService;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.nbakalov.flowerscompany.services.validators.UserServiceModelValidatorService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.LogConstants.*;
import static org.nbakalov.flowerscompany.constants.RoleConstants.*;
import static org.nbakalov.flowerscompany.constants.UserConstants.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final RoleService roleService;
  private final ModelMapper modelMapper;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserServiceModelValidatorService validatorService;
  private final LogService logService;

  @Override
  public UserServiceModel registerUser(UserServiceModel userServiceModel) {

    if (!validatorService.isValid(userServiceModel)) {
      throw new IllegalOrderServiceModelException(USER_BAD_CREDENTIALS);
    }

    if (userRepository.findByUsername(userServiceModel.getUsername()).isPresent()) {
      throw new UserAllreadyExistException(USER_ALLREADY_EXIST);
    }

    if (userRepository.findByEmail(userServiceModel.getEmail()).isPresent()) {
      throw new UserWithThisEmailAllreadyExist(USER_ALLREADY_WITH_EMAIL_EXIST);
    }

    if (userRepository.count() == 0) {
      roleService.seedRolesInDb();
      userServiceModel.setAuthorities(roleService.findAllRoles());
    } else {
      userServiceModel.setAuthorities(new LinkedHashSet<>());
      userServiceModel.getAuthorities()
              .add(roleService.findByAuthority(CUSTOMER));
    }

    User user = modelMapper.map(userServiceModel, User.class);
    user.setPassword(bCryptPasswordEncoder.encode(userServiceModel.getPassword()));
    userRepository.saveAndFlush(user);

    LogServiceModel log = createLog(user.getUsername(), REGISTERED_USER);

    logService.saveLog(log);

    return modelMapper.map(user, UserServiceModel.class);
  }

  @Override
  public UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword) {

    User user = userRepository.findByUsername(userServiceModel.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

    if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
      throw new WrongPasswordException(WRONG_PASSWORD);
    }

    user.setPassword(userServiceModel.getPassword() != null
            ? bCryptPasswordEncoder.encode(userServiceModel.getPassword())
            : user.getPassword());

    user.setEmail(userServiceModel.getEmail());

    userRepository.saveAndFlush(user);

    LogServiceModel log = createLog(user.getUsername(), EDITED_USER);

    logService.saveLog(log);

    return modelMapper.map(user, UserServiceModel.class);
  }

  @Override
  public UserServiceModel findByUsername(String username) {
    return userRepository.findByUsername(username)
            .map(user -> modelMapper.map(user, UserServiceModel.class))
            .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
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
            .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

    UserServiceModel userServiceModel = modelMapper.map(user, UserServiceModel.class);

    userServiceModel.getAuthorities().clear();

    LogServiceModel log = new LogServiceModel();

    if (role.equals("operator")) {
      userServiceModel.getAuthorities().add(roleService.findByAuthority(OPERATOR));
      log = createLog(user.getUsername(), PROMOTED_OPERATOR);
    } else if (role.equals("admin")) {
      userServiceModel.getAuthorities().add(roleService.findByAuthority(OPERATOR));
      userServiceModel.getAuthorities().add(roleService.findByAuthority(ADMIN));

      log = createLog(user.getUsername(), PROMOTED_ADMIN);
    }

    logService.saveLog(log);

    userRepository.saveAndFlush(modelMapper.map(userServiceModel, User.class));
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
  }

  private LogServiceModel createLog(String username, String description) {

    LogServiceModel logModel = new LogServiceModel();
    logModel.setCreatedOn(NOW);
    logModel.setUsername(username);
    logModel.setDescription(description);

    return logModel;
  }
}
