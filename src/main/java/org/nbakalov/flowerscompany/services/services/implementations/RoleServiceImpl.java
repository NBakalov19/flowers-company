package org.nbakalov.flowerscompany.services.services.implementations;

import static org.nbakalov.flowerscompany.constants.RoleConstants.*;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Role;
import org.nbakalov.flowerscompany.data.repositories.RoleRepository;
import org.nbakalov.flowerscompany.services.models.RoleServiceModel;
import org.nbakalov.flowerscompany.services.services.RoleService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {


  private final RoleRepository roleRepository;
  private final ModelMapper modelMapper;

  @Override
  public void seedRolesInDb() {
    if (roleRepository.count() == 0) {
      roleRepository.saveAndFlush(new Role(ROLE_CUSTOMER));
      roleRepository.saveAndFlush(new Role(ROLE_OPERATOR));
      roleRepository.saveAndFlush(new Role(ROLE_ADMIN));
      roleRepository.saveAndFlush(new Role(ROLE_ROOT));
    }
  }

  @Override
  public Set<RoleServiceModel> findAllRoles() {
    return roleRepository.findAll()
            .stream()
            .map(role -> modelMapper.map(role, RoleServiceModel.class))
            .collect(Collectors.toSet());
  }

  @Override
  public RoleServiceModel findByAuthority(String authority) {
    return modelMapper.map(roleRepository.findByAuthority(authority), RoleServiceModel.class);
  }
}
