package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.RoleServiceModel;

import java.util.Set;

public interface RoleService {

  RoleServiceModel findByAuthority(String authority);

  Set<RoleServiceModel> findAllRoles();

  void seedRolesInDb();
}
