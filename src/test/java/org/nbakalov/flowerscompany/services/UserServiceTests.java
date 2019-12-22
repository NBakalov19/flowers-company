package org.nbakalov.flowerscompany.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Role;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.nbakalov.flowerscompany.data.repositories.UserRepository;
import org.nbakalov.flowerscompany.errors.WrongPasswordException;
import org.nbakalov.flowerscompany.errors.dublicates.UserAllreadyExistException;
import org.nbakalov.flowerscompany.errors.dublicates.UserWithThisEmailAllreadyExist;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalUserServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.UserNotFoundException;
import org.nbakalov.flowerscompany.services.models.RoleServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.RoleService;
import org.nbakalov.flowerscompany.services.services.implementations.UserServiceImpl;
import org.nbakalov.flowerscompany.services.validators.UserServiceModelValidatorService;
import org.nbakalov.flowerscompany.services.validators.implementation.UserServiceModelValidatorServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UserServiceTests {

  private static final UserServiceModel MODEL = new UserServiceModel();
  private static final User USER = new User();

  private static final String VALID_USERNAME = "Niko";
  private static final String VALID_PASSWORD = "123456";
  private static final String VALID_EDITED_PASSWORD = "1234567";
  private static final String VALID_EMAIL = "dj_as@abv.bg";
  private static final String VALID_ID = "id";
  private static final String VALID_EDITED_EMAIL = "dj_abv@abv.bg";

  private static final String NEW_IMAGE_URL = "[random_url]";

  @InjectMocks
  UserServiceImpl userService;

  @Mock
  UserRepository userRepository;

  @Mock
  RoleService roleService;

  @Mock
  ModelMapper modelMapper;

  @Mock
  BCryptPasswordEncoder encoder;

  @Mock
  LogService logService;

  @Mock
  UserServiceModelValidatorService validatorService;


  @Before
  public void init() throws RoleNotFoundException {

    UserServiceModelValidatorServiceImpl actualUserValidation = new UserServiceModelValidatorServiceImpl();
    ModelMapper actualMapper = new ModelMapper();
    BCryptPasswordEncoder actualEncoder = new BCryptPasswordEncoder();

    when(modelMapper.map(any(UserServiceModel.class), eq(User.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], User.class));

    when(modelMapper.map(any(User.class), eq(UserServiceModel.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], UserServiceModel.class));

    when(roleService.findByAuthority(anyString()))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(new Role((String) invocationOnMock.getArguments()[0]),
                            RoleServiceModel.class));

    when(validatorService.isValid(any()))
            .thenAnswer(invocationOnMock ->
                    actualUserValidation.isValid((UserServiceModel) invocationOnMock.getArguments()[0]));

    when(logService.saveLog(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

    when(encoder.encode(any())).thenAnswer(invocationOnMock ->
            actualEncoder.encode((CharSequence) invocationOnMock.getArguments()[0]));
    when(encoder.matches(any(), any())).thenAnswer(invocationOnMock ->
            actualEncoder.matches((String) invocationOnMock.getArguments()[0],
                    (String) invocationOnMock.getArguments()[1]));

    USER.setUsername(VALID_USERNAME);
    USER.setPassword(VALID_PASSWORD);
    USER.setEmail(VALID_EMAIL);
    USER.setProfilePictureUrl(NEW_IMAGE_URL);

    MODEL.setUsername(VALID_USERNAME);
    MODEL.setPassword(VALID_PASSWORD);
    MODEL.setEmail(VALID_EMAIL);
    MODEL.setAuthorities(Set.of(new RoleServiceModel()));
  }

  @Test(expected = IllegalUserServiceModelException.class)
  public void registerUser_WhenNotValid_ShouldThrow() throws RoleNotFoundException {

    Mockito.when(validatorService.isValid(MODEL))
            .thenReturn(false);

    userService.registerUser(MODEL);
  }

  @Test
  public void registerUser_WhenFirstValidUserIsAdded_ShouldWork() throws RoleNotFoundException {

    when(userRepository.count()).thenReturn(0L);
    when(validatorService.isValid(MODEL)).thenReturn(true);
    when(userRepository.saveAndFlush(any(User.class))).thenReturn(USER);

    UserServiceModel serviceModel = userService.registerUser(MODEL);

    assertNotNull(MODEL);
    assertEquals(USER.getUsername(), serviceModel.getUsername());
  }

  @Test
  public void registerUser_WhenNotFirstValidUserIsAdded_ShouldWork() throws RoleNotFoundException {

    String authority = "ROLE_CUSTOMER";
    RoleServiceModel mockRole = new RoleServiceModel() {{
      new Role(authority);
    }};

    when(userRepository.count()).thenReturn(1L);
    when(userRepository.saveAndFlush(any(User.class))).thenReturn(USER);
    when(roleService.findByAuthority(authority)).thenReturn(mockRole);

    UserServiceModel serviceModel = userService.registerUser(MODEL);

    assertNotNull(serviceModel);
    assertEquals(USER.getUsername(), serviceModel.getUsername());

  }

  @Test(expected = UsernameNotFoundException.class)
  public void findByUsername_WhenNotExistUser_ShouldThrow() {
    String username = "Peter";

    userService.findByUsername(username);
  }

  @Test
  public void findByUsername_WhenUserExist_ShouldWork() {

    when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(USER));

    UserServiceModel serviceModel = userService.findByUsername(VALID_USERNAME);

    assertEquals(VALID_USERNAME, serviceModel.getUsername());
  }

  @Test(expected = UsernameNotFoundException.class)
  public void loadByUsername_WhenNotExistUser_ShouldThrow() {
    String username = "Peter";

    userService.loadUserByUsername(username);
  }

  @Test
  public void loadByUsername_WhenUserExist_ShouldWork() {

    when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(USER));

    UserServiceModel serviceModel = userService.findByUsername(VALID_USERNAME);

    assertEquals(VALID_USERNAME, serviceModel.getUsername());
  }

  @Test(expected = IllegalUserServiceModelException.class)
  public void registerUser_WhenModelIsNotValid_ShouldThrow() throws RoleNotFoundException {

    when(validatorService.isValid(MODEL)).thenReturn(false);

    userService.registerUser(MODEL);
  }

  @Test(expected = UserAllreadyExistException.class)
  public void registerUser_WhenUserIsExist_ShouldThrow() throws RoleNotFoundException {

    when(userRepository.findByUsername(USER.getUsername())).thenReturn(Optional.of(USER));

    userService.registerUser(MODEL);
  }

  @Test(expected = UserWithThisEmailAllreadyExist.class)
  public void registerUser_WhenEmailIsExist_ShouldThrow() throws RoleNotFoundException {

    when(userRepository.findByEmail(USER.getEmail())).thenReturn(Optional.of(USER));

    userService.registerUser(MODEL);
  }

  @Test
  public void findAllUser_WhenNotHaveUsers_ShouldReturnEmptyList() {

    List<User> allUsers = userRepository.findAll();

    assertEquals(0, allUsers.size());
  }


  @Test(expected = UsernameNotFoundException.class)
  public void editUserProfile_WhenUserNotExist() {

    userService.editUserProfile(MODEL, VALID_PASSWORD);
  }

  @Test(expected = WrongPasswordException.class)
  public void editUserProfile_WhenUserOldPasswordNotMatch() {

    when(userRepository.findByUsername(VALID_USERNAME)).thenReturn(Optional.of(USER));

    userService.editUserProfile(MODEL, VALID_EDITED_PASSWORD);
  }

  @Test(expected = UserNotFoundException.class)
  public void setUserRole_WhenUserWithIdNotExist_ShouldThrow() throws RoleNotFoundException {

    String invalidId = "id1";
    String role = "operator";

    userService.setUserRole(invalidId, role);
  }

  @Test
  public void setUserRole_WhenUserIsPromotedToOperator_ShouldHaveRoleOperator() throws RoleNotFoundException {

    Set<Role> auth = Set.of(new Role("ROLE_CUSTOMER"));
    String role = "operator";
    USER.setAuthorities(auth);

    when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(USER));

    UserServiceModel currUser = userService.setUserRole(VALID_ID, role);

    List<RoleServiceModel> currUserAuthorities = new ArrayList<>(currUser.getAuthorities());

    assertEquals(1, currUser.getAuthorities().size());
    assertEquals("ROLE_OPERATOR", currUserAuthorities.get(0).getAuthority());
  }

  @Test
  public void setUserRole_WhenUserIsPromotedToAdmin_ShouldHaveRolesAndAdmin() throws RoleNotFoundException {

    Set<Role> auth = Set.of(new Role("ROLE_CUSTOMER"));
    String role = "admin";
    USER.setAuthorities(auth);

    when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(USER));

    UserServiceModel currUser = userService.setUserRole(VALID_ID, role);

    List<String> currUserAuthorities = currUser.getAuthorities().stream()
            .map(RoleServiceModel::getAuthority)
            .sorted()
            .collect(Collectors.toList());

    assertEquals(2, currUser.getAuthorities().size());
    assertEquals("ROLE_ADMIN", currUserAuthorities.get(0));
    assertEquals("ROLE_OPERATOR", currUserAuthorities.get(1));
  }
}
