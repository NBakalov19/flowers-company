package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Role;
import org.nbakalov.flowerscompany.data.repositories.RoleRepository;
import org.nbakalov.flowerscompany.services.models.RoleServiceModel;
import org.nbakalov.flowerscompany.services.services.RoleService;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.RoleConstants.*;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final ModelMapper modelMapper;

  @Override
  public RoleServiceModel findByAuthority(String authority) throws RoleNotFoundException {
    return roleRepository.findByAuthority(authority)
            .map(role -> modelMapper.map(role, RoleServiceModel.class))
            .orElseThrow(() -> new RoleNotFoundException("Role not found."));
  }

  @Override
  public Set<RoleServiceModel> findAllRoles() {
    return roleRepository.findAll()
            .stream()
            .map(role -> modelMapper.map(role, RoleServiceModel.class))
            .collect(Collectors.toSet());
  }

  @Override
  public void seedRolesInDb() {
    if (roleRepository.count() == 0) {
      roleRepository.saveAndFlush(new Role(CUSTOMER));
      roleRepository.saveAndFlush(new Role(OPERATOR));
      roleRepository.saveAndFlush(new Role(ADMIN));
      roleRepository.saveAndFlush(new Role(ROOT));
    }
  }
}