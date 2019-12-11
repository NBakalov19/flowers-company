package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.RoleServiceModel;

import java.util.Set;

public interface RoleService {

  void seedRolesInDb();

  Set<RoleServiceModel> findAllRoles();

  RoleServiceModel findByAuthority(String authority);
}
