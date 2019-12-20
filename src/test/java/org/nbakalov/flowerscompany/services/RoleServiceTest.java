package org.nbakalov.flowerscompany.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Role;
import org.nbakalov.flowerscompany.data.repositories.RoleRepository;
import org.nbakalov.flowerscompany.services.models.RoleServiceModel;
import org.nbakalov.flowerscompany.services.services.implementations.RoleServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class RoleServiceTest {

  private static final String ROLE_ROOT = "ROLE_ROOT";
  private static final String ROLE_ADMIN = "ROLE_ADMIN";
  private static final String ROLE_OPERATOR = "ROLE_OPERATOR";
  private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

  private static List<Role> fakeRepository;

  @InjectMocks
  private RoleServiceImpl roleService;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private ModelMapper modelMapper;

  @Before
  public void init() {

    fakeRepository = new ArrayList<>();

    when(roleRepository.saveAndFlush(any(Role.class)))
            .thenAnswer(invocationOnMock -> {
              fakeRepository.add((Role) invocationOnMock.getArguments()[0]);
              return null;
            });

    ModelMapper actualMapper = new ModelMapper();
    when(modelMapper.map(any(Role.class), eq(RoleServiceModel.class)))
            .thenAnswer(invocationOnMock -> actualMapper.map(invocationOnMock.getArguments()[0], RoleServiceModel.class));
  }

  @Test
  public void seedRoles_shouldSeedAllRoles_whenRepositoryEmpty() {


    when(roleRepository.count()).thenReturn(0L);

    roleService.seedRolesInDb();

    fakeRepository = fakeRepository.stream()
            .sorted(Comparator.comparing(Role::getAuthority)).collect(Collectors.toList());

    int expected = 4;
    int actual = fakeRepository.size();
    assertEquals(expected, actual);
    assertEquals(fakeRepository.get(0).getAuthority(), ROLE_ADMIN);
    assertEquals(fakeRepository.get(1).getAuthority(), ROLE_CUSTOMER);
    assertEquals(fakeRepository.get(2).getAuthority(), ROLE_OPERATOR);
    assertEquals(fakeRepository.get(3).getAuthority(), ROLE_ROOT);
  }

  @Test
  public void seedRoles_shouldDoNothing_whenRepositoryNotEmpty() {

    when(roleRepository.count()).thenReturn(4L);

    roleService.seedRolesInDb();

    int expected = 0;
    int actual = fakeRepository.size();
    assertEquals(expected, actual);
  }


  @Test
  public void findAll_shouldReturnAllServiceModelsCorrect_whenAnyRolesInDb() {


    List<Role> allRoles = Arrays
            .asList(new Role(ROLE_ROOT), new Role(ROLE_ADMIN), new Role(ROLE_CUSTOMER), new Role(ROLE_OPERATOR));
    when(roleRepository.findAll()).thenReturn(allRoles);


    Set<RoleServiceModel> allFoundRoleServiceModels = roleService.findAllRoles();


    int expected = allRoles.size();
    int actual = allFoundRoleServiceModels.size();
    assertEquals(expected, actual);
  }

  @Test
  public void findAll_shouldReturnEmptyCollection_whenNoRolesInDb() {


    List<Role> allRoles = new ArrayList<>();
    when(roleRepository.findAll()).thenReturn(allRoles);

    Set<RoleServiceModel> allFoundRoleServiceModels = roleService.findAllRoles();

    int expected = allRoles.size();
    int actual = allFoundRoleServiceModels.size();
    assertEquals(expected, actual);
  }
}
